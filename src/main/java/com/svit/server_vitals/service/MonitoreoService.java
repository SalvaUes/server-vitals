package com.svit.server_vitals.service;

import com.svit.server_vitals.dto.SystemResourceDto;
import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class MonitoreoService {

    private static final Logger log = LoggerFactory.getLogger(MonitoreoService.class);

    private final UmbralRepository umbralRepository;
    private final AlertaRepository alertaRepository;
    private final MailService mailService;
    private final SystemMonitorService systemMonitorService;

    public MonitoreoService(UmbralRepository umbralRepository,
                            AlertaRepository alertaRepository,
                            MailService mailService,
                            SystemMonitorService systemMonitorService) {
        this.umbralRepository = umbralRepository;
        this.alertaRepository = alertaRepository;
        this.mailService = mailService;
        this.systemMonitorService = systemMonitorService;
    }

    @Scheduled(fixedRate = 300000) // Ejecuta cada 5 minutos
    public void monitorearRecursos() {
        log.debug("Ejecutando monitorearRecursos...");
        List<Umbral> umbrales = umbralRepository.findAll();
        SystemResourceDto metricasActuales = systemMonitorService.getLatestMetrics();

        if (metricasActuales == null) {
            log.warn("No se pudieron obtener las métricas actuales del sistema.");
            return;
        }

        Map<String, Integer> valoresActuales = new HashMap<>();
        valoresActuales.put("CPU", metricasActuales.getCpuUsage());
        valoresActuales.put("RAM", metricasActuales.getMemoryUsage());
        valoresActuales.put("DISCO", metricasActuales.getDiskUsage());

        log.debug("Valores actuales - CPU: {}%, RAM: {}%, DISCO: {}%",
                  valoresActuales.get("CPU"), valoresActuales.get("RAM"), valoresActuales.get("DISCO"));

        if (mailService == null) {
             log.warn("MailService no está inyectado en MonitoreoService. No se podrán enviar alertas.");
             return;
        }

        for (Umbral umbral : umbrales) {
            String tipoRecurso = umbral.getTipoRecurso();
            double valorMaximoUmbral = umbral.getValorMaximo();

            if (!valoresActuales.containsKey(tipoRecurso)) {
                log.warn("No se encontró un valor actual para el tipo de recurso del umbral: {}", tipoRecurso);
                continue;
            }

            double valorRecursoActual = valoresActuales.get(tipoRecurso);

            log.debug("Verificando umbral para: {} (Umbral Máximo: {}, Valor Actual: {})",
                      tipoRecurso, valorMaximoUmbral, valorRecursoActual);

            if (valorRecursoActual > valorMaximoUmbral) {
                log.warn("¡Umbral SUPERADO para {}! Valor actual ({}) > Umbral ({})",
                         tipoRecurso, valorRecursoActual, valorMaximoUmbral);

                List<Alerta> alertasConfiguradas = alertaRepository.findByTipoRecurso(tipoRecurso);
                log.info("Se encontraron {} configuraciones de alerta para {}", alertasConfiguradas.size(), tipoRecurso);

                if (alertasConfiguradas.isEmpty()) {
                    log.warn("Umbral superado para {}, pero no hay alertas configuradas para notificar.", tipoRecurso);
                    continue;
                }

                for (Alerta alerta : alertasConfiguradas) {
                    String destinatario = alerta.getCorreoDestino();
                    
                    String asunto = "Alerta SVITS: Umbral Superado para " + tipoRecurso;
                    String mensaje = alerta.getMensaje()
                                         .replace("${tipoRecurso}", tipoRecurso) 
                                         .replace("${valorActual}", String.valueOf(valorRecursoActual))
                                         .replace("${umbralMaximo}", String.valueOf(valorMaximoUmbral));
                    

                    log.info("Enviando alerta para {} a {}...", tipoRecurso, destinatario);
                    mailService.enviarCorreoAlerta(destinatario, asunto, mensaje);
                }

            } else {
                log.debug("Umbral OK para {}. Valor actual ({}) <= Umbral ({})",
                          tipoRecurso, valorRecursoActual, valorMaximoUmbral);
            }
        }
        log.debug("Fin de la ejecución de monitorearRecursos.");
    }
}
