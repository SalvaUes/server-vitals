package com.svit.server_vitals.service;

import com.svit.server_vitals.dto.SystemResourceDto;
import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.model.MetricaHistorial;
import com.svit.server_vitals.model.NivelAlerta;
import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.repository.MetricaHistorialRepository;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoreoService {

    private static final Logger log = LoggerFactory.getLogger(MonitoreoService.class);

    private final UmbralRepository umbralRepository;
    private final AlertaRepository alertaRepository;
    private final MailService mailService;
    private final SystemMonitorService systemMonitorService;
    private final MetricaHistorialRepository metricaHistorialRepository;

    public MonitoreoService(UmbralRepository umbralRepository,
                             AlertaRepository alertaRepository,
                             MailService mailService,
                             SystemMonitorService systemMonitorService,
                             MetricaHistorialRepository metricaHistorialRepository) {
        this.umbralRepository = umbralRepository;
        this.alertaRepository = alertaRepository;
        this.mailService = mailService;
        this.systemMonitorService = systemMonitorService;
        this.metricaHistorialRepository = metricaHistorialRepository;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void monitorearYGuardarHistorial() {
        log.info("Iniciando tarea programada de monitoreo...");

        SystemResourceDto metricasActuales = systemMonitorService.getLatestMetrics();
        if (metricasActuales == null) {
            log.warn("No se pudieron obtener las métricas actuales del sistema.");
            return;
        }

        double usoCpu = metricasActuales.getCpuUsage();
        double usoRam = metricasActuales.getMemoryUsage();
        double usoDisco = metricasActuales.getDiskUsage();

        List<Umbral> umbrales = umbralRepository.findAll();
        
        NivelAlerta nivelMasAltoAlcanzado = determinarNivelAlertaMasAlto(usoCpu, usoRam, usoDisco, umbrales);

        guardarMetricaEnHistorial(usoCpu, usoRam, usoDisco, nivelMasAltoAlcanzado);
        
        if (nivelMasAltoAlcanzado != NivelAlerta.NORMAL) {
            log.warn("¡Nivel de alerta detectado: {}! Preparando envío de correos.", nivelMasAltoAlcanzado);
            
            Optional<Umbral> umbralCritico = encontrarUmbralParaNivel(nivelMasAltoAlcanzado, usoCpu, usoRam, usoDisco, umbrales);
            
            umbralCritico.ifPresent(umbral -> enviarAlertasPorCorreo(umbral, usoCpu, usoRam, usoDisco));
        } else {
            log.info("El estado del sistema es NORMAL.");
        }
        log.info("Fin de la tarea programada.");
    }

    

    private void guardarMetricaEnHistorial(double usoCpu, double usoRam, double usoDisco, NivelAlerta nivel) {
        MetricaHistorial metrica = new MetricaHistorial();
        metrica.setFechaHora(LocalDateTime.now());
        metrica.setUsoCpu(usoCpu);
        metrica.setUsoRam(usoRam);
        metrica.setUsoDisco(usoDisco);
        metrica.setNivelAlerta(nivel);
        metricaHistorialRepository.save(metrica);
        log.info("Métrica guardada en el historial con nivel de alerta: {}", nivel);
    }
    
    private NivelAlerta determinarNivelAlertaMasAlto(double usoCpu, double usoRam, double usoDisco, List<Umbral> umbrales) {
        return umbrales.stream()
            .filter(umbral -> umbralSuperado(umbral, usoCpu, usoRam, usoDisco))
            .map(Umbral::getNivelAlerta)
            .max(Comparator.comparing(Enum::ordinal))
            .orElse(NivelAlerta.NORMAL);
    }

    private boolean umbralSuperado(Umbral umbral, double usoCpu, double usoRam, double usoDisco) {
        String tipoRecurso = umbral.getTipoRecurso().toUpperCase();
        double porcentajeUmbral = umbral.getPorcentaje();
        switch (tipoRecurso) {
            case "CPU": return usoCpu > porcentajeUmbral;
            case "RAM": return usoRam > porcentajeUmbral;
            case "DISCO": return usoDisco > porcentajeUmbral;
            default: return false;
        }
    }

    private Optional<Umbral> encontrarUmbralParaNivel(NivelAlerta nivel, double usoCpu, double usoRam, double usoDisco, List<Umbral> umbrales) {
         return umbrales.stream()
            .filter(umbral -> umbral.getNivelAlerta() == nivel && umbralSuperado(umbral, usoCpu, usoRam, usoDisco))
            .findFirst();
    }
    
   
    private void enviarAlertasPorCorreo(Umbral umbralSuperado, double usoCpu, double usoRam, double usoDisco) {
        String recursoAfectado = umbralSuperado.getTipoRecurso();
        log.info("El recurso afectado es: {}. Buscando destinatarios...", recursoAfectado);

        
        List<Alerta> alertasConfiguradas = alertaRepository.findByTipoRecurso(recursoAfectado);
        
        if (alertasConfiguradas.isEmpty()) {
            log.warn("Alerta de {} detectada, pero no hay correos configurados para este recurso.", recursoAfectado);
            return;
        }

        
        String asunto = String.format("Alerta de Sistema SVITS: Nivel %s en %s", umbralSuperado.getNivelAlerta(), recursoAfectado);
        
        double valorActual = 0;
        if ("CPU".equals(recursoAfectado)) valorActual = usoCpu;
        if ("RAM".equals(recursoAfectado)) valorActual = usoRam;
        if ("DISCO".equals(recursoAfectado)) valorActual = usoDisco;

        String mensaje = String.format(
            "Se ha detectado una alerta de nivel %s.\n\n" +
            "Recurso: %s\n" +
            "Umbral Superado: %.2f%%\n" +
            "Valor Actual: %.2f%%\n\n" +
            "Estado general del sistema:\n" +
            "- CPU: %.2f%%\n" +
            "- RAM: %.2f%%\n" +
            "- Disco: %.2f%%",
            umbralSuperado.getNivelAlerta(), recursoAfectado, umbralSuperado.getPorcentaje(), valorActual,
            usoCpu, usoRam, usoDisco
        );

        
        for (Alerta alerta : alertasConfiguradas) {
            String destinatario = alerta.getCorreoDestino();
            log.info("Enviando correo de alerta a: {}", destinatario);
            try {
                mailService.enviarCorreoAlerta(destinatario, asunto, mensaje);
            } catch (Exception e) {
                log.error("Fallo al enviar correo a {}: {}", destinatario, e.getMessage());
            }
        }
    }
    
}