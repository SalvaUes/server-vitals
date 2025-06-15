package com.svit.server_vitals.service;

import com.svit.server_vitals.dto.SystemResourceDto;
import com.svit.server_vitals.model.*;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.repository.MetricaHistorialRepository;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class MonitoreoService {

    private static final Logger log = LoggerFactory.getLogger(MonitoreoService.class);

    private final UmbralRepository umbralRepository;
    private final AlertaRepository alertaRepository;
    private final MailService mailService;
    private final SystemMonitorService systemMonitorService;
    private final MetricaHistorialRepository metricaHistorialRepository;

    public MonitoreoService(UmbralRepository umbralRepository, AlertaRepository alertaRepository, MailService mailService, SystemMonitorService systemMonitorService, MetricaHistorialRepository metricaHistorialRepository) {
        this.umbralRepository = umbralRepository;
        this.alertaRepository = alertaRepository;
        this.mailService = mailService;
        this.systemMonitorService = systemMonitorService;
        this.metricaHistorialRepository = metricaHistorialRepository;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void revisarSistemaYNotificar() {
        log.info("Iniciando ciclo de monitoreo y notificación...");
        SystemResourceDto metricasActuales = systemMonitorService.getLatestMetrics();
        if (metricasActuales == null) {
            log.warn("No se pudieron obtener las métricas del sistema. Se omite este ciclo.");
            return;
        }
        guardarMetricaEnHistorial(metricasActuales);
        List<Umbral> todosLosUmbrales = umbralRepository.findAll();
        List<Umbral> umbralesSuperados = todosLosUmbrales.stream()
            .filter(umbral -> umbralSuperado(umbral, metricasActuales))
            .collect(Collectors.toList());
        if (umbralesSuperados.isEmpty()) {
            log.info("Todos los recursos están dentro de los umbrales. Fin del ciclo.");
            return;
        }
        log.warn("Se detectaron {} umbrales superados. Procesando notificaciones...", umbralesSuperados.size());
        for (Umbral umbral : umbralesSuperados) {
            procesarNotificacionesParaRecurso(umbral.getTipoRecurso(), metricasActuales);
        }
        log.info("Ciclo de monitoreo y notificación finalizado.");
    }

    private void procesarNotificacionesParaRecurso(String tipoRecurso, SystemResourceDto metricasActuales) {
        List<Alerta> alertasParaRecurso = alertaRepository.findByTipoRecurso(tipoRecurso);
        for (Alerta alerta : alertasParaRecurso) {
            LocalDateTime ultimoEnvio = alerta.getUltimaNotificacionEnviada();
            Integer intervalo = alerta.getIntervaloMinutos();
            if (ultimoEnvio == null || ultimoEnvio.plusMinutes(intervalo).isBefore(LocalDateTime.now())) {
                log.info("¡Es hora de notificar! Enviando alerta para {} al correo {}.", tipoRecurso, alerta.getCorreoDestino());
                String asunto = String.format("Alerta de Sistema SVITS: %s", tipoRecurso);
                String cuerpo = construirCuerpoCorreo(alerta.getMensaje(), metricasActuales);
                mailService.enviarCorreoAlerta(alerta.getCorreoDestino(), asunto, cuerpo);
                alerta.setUltimaNotificacionEnviada(LocalDateTime.now());
                alertaRepository.save(alerta);
                log.info("Fecha de último envío actualizada para la alerta ID: {}", alerta.getId());
            } else {
                log.info("Alerta de {} detectada, pero se respeta el intervalo de {} min para el correo {}. No se envía ahora.", tipoRecurso, intervalo, alerta.getCorreoDestino());
            }
        }
    }
    
    
    
    private String construirCuerpoCorreo(String mensajePersonalizado, SystemResourceDto metricas) {
       
        
        return String.format(
            "%s\n\n" +
            "--- Estado Actual del Sistema ---\n" +
            "- CPU: %.2f%%\n" +
            "- RAM: %.2f%%\n" +
            "- Disco: %.2f%%",
            mensajePersonalizado,
            (double) metricas.getCpuUsage(),
            (double) metricas.getMemoryUsage(),
            (double) metricas.getDiskUsage()
        );
    }
    
    
    
    private void guardarMetricaEnHistorial(SystemResourceDto metricas) {
        List<Umbral> umbrales = umbralRepository.findAll();
        NivelAlerta nivelActual = umbrales.stream()
            .filter(umbral -> umbralSuperado(umbral, metricas))
            .map(Umbral::getNivelAlerta)
            .max(Comparator.comparing(Enum::ordinal))
            .orElse(NivelAlerta.NORMAL);

        MetricaHistorial metrica = new MetricaHistorial();
        metrica.setFechaHora(LocalDateTime.now());
        metrica.setUsoCpu(metricas.getCpuUsage());
        metrica.setUsoRam(metricas.getMemoryUsage());
        metrica.setUsoDisco(metricas.getDiskUsage());
        metrica.setNivelAlerta(nivelActual);
        metricaHistorialRepository.save(metrica);
    }

    private boolean umbralSuperado(Umbral umbral, SystemResourceDto metricas) {
        String tipoRecurso = umbral.getTipoRecurso().toUpperCase();
        double porcentajeUmbral = umbral.getPorcentaje();
        switch (tipoRecurso) {
            case "CPU": return metricas.getCpuUsage() > porcentajeUmbral;
            case "RAM": return metricas.getMemoryUsage() > porcentajeUmbral;
            case "DISCO": return metricas.getDiskUsage() > porcentajeUmbral;
            default: return false;
        }
    }
}