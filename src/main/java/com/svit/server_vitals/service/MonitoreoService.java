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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public void recopilarYGuardarMetricas() {
        log.info("Recopilando y guardando métricas...");
        SystemResourceDto metricasActuales = systemMonitorService.getLatestMetrics();
        if (metricasActuales == null) { return; }
        guardarMetricaEnHistorial(metricasActuales);
    }
    
    @Scheduled(cron = "0 */5 * * * ?")
    public void revisarYNotificarAlertas() {
        log.info("Iniciando ciclo de revisión de notificaciones...");
        List<Alerta> todasLasAlertasConfiguradas = alertaRepository.findAll();
        for (Alerta alerta : todasLasAlertasConfiguradas) {
            if (esHoraDeRevisar(alerta)) {
                procesarReporteParaAlerta(alerta);
            }
        }
    }

    private boolean esHoraDeRevisar(Alerta alerta) {
        LocalDateTime ultimoEnvio = alerta.getUltimaNotificacionEnviada();
        Integer intervalo = alerta.getIntervaloMinutos();
        if (ultimoEnvio == null || ultimoEnvio.plusMinutes(intervalo).isBefore(LocalDateTime.now())) {
            log.info("Es hora de revisar las alertas para {} (recurso: {}).", alerta.getCorreoDestino(), alerta.getTipoRecurso());
            return true;
        }
        return false;
    }

    private void procesarReporteParaAlerta(Alerta alerta) {
        Optional<Umbral> umbralOpt = umbralRepository.findByTipoRecurso(alerta.getTipoRecurso());
        if (umbralOpt.isEmpty()) {
            log.warn("No se encontró un umbral para el recurso {}, no se puede procesar la alerta ID {}.", alerta.getTipoRecurso(), alerta.getId());
            return;
        }
        Umbral umbral = umbralOpt.get();

        LocalDateTime fechaDesde = (alerta.getUltimaNotificacionEnviada() != null) 
            ? alerta.getUltimaNotificacionEnviada() 
            : LocalDateTime.now().minusMinutes(alerta.getIntervaloMinutos());
            
        List<MetricaHistorial> historialRelevante = metricaHistorialRepository.findByFechaHoraAfter(fechaDesde);

        List<MetricaHistorial> metricasSuperadas = historialRelevante.stream()
            .filter(metrica -> valorSuperaUmbral(alerta.getTipoRecurso(), metrica, umbral))
            .collect(Collectors.toList());

        if (!metricasSuperadas.isEmpty()) {
            String cuerpoCorreo = construirCuerpoCorreoResumen(alerta, umbral, metricasSuperadas, fechaDesde); // Se pasa la fecha de inicio
            String asunto = String.format("Resumen de Alertas SVITS: %s", alerta.getTipoRecurso());
            mailService.enviarCorreoAlerta(alerta.getCorreoDestino(), asunto, cuerpoCorreo);
            alerta.setUltimaNotificacionEnviada(LocalDateTime.now());
            alertaRepository.save(alerta);
            log.info("Reporte enviado y fecha actualizada para alerta ID {}", alerta.getId());
        } else {
            log.info("No se superó el umbral para {} en el periodo revisado para la alerta ID {}.", alerta.getTipoRecurso(), alerta.getId());
            
            
            alerta.setUltimaNotificacionEnviada(LocalDateTime.now());
            alertaRepository.save(alerta);
        }
    }

    
    private String construirCuerpoCorreoResumen(Alerta alerta, Umbral umbral, List<MetricaHistorial> metricasSuperadas, LocalDateTime fechaDesde) {
        MetricaHistorial picoMetrica = metricasSuperadas.stream()
            .max(Comparator.comparing(m -> getValorPorRecurso(alerta.getTipoRecurso(), m)))
            .orElse(null);

        double picoMaximo = 0;
        String horaPico = "N/A";
        if (picoMetrica != null) {
            picoMaximo = getValorPorRecurso(alerta.getTipoRecurso(), picoMetrica);
            horaPico = picoMetrica.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        SystemResourceDto metricasActuales = systemMonitorService.getLatestMetrics();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime fechaHasta = LocalDateTime.now();

        
        
        return String.format(
            "%s\n\n" +
            "===============================================================\n" +
            "Resumen de Alerta para el Recurso: %s\n" +
            "Periodo Analizado: %s al %s\n" +
            "Frecuencia de Notificación Configurada: Cada %d minutos\n" +
            "===============================================================\n\n" +
            "Se ha detectado un comportamiento anómalo que requiere tu atención.\n\n" +
            "- Nivel de Alerta Alcanzado: %s\n" +
            "- Umbral Configurado: > %.2f%%\n" +
            "- Pico Máximo Registrado: %.2f%% (alcanzado a las %s)\n" + 
            "- Número de Veces Superado: %d veces\n\n" +
            "--- Estado del Sistema al momento del envío (%s) ---\n" +
            "- CPU: %.2f%%\n" +
            "- RAM: %.2f%%\n" +
            "- Disco: %.2f%%",
            alerta.getMensaje(),
            alerta.getTipoRecurso(),
            fechaDesde.format(formatter),
            fechaHasta.format(formatter),
            alerta.getIntervaloMinutos(),
            umbral.getNivelAlerta(),
            (double) umbral.getPorcentaje(),
            picoMaximo,
            horaPico,
            metricasSuperadas.size(),
            fechaHasta.format(DateTimeFormatter.ofPattern("HH:mm")),
            (double) metricasActuales.getCpuUsage(),
            (double) metricasActuales.getMemoryUsage(),
            (double) metricasActuales.getDiskUsage()
        );
    }
    
    private double getValorPorRecurso(String tipoRecurso, MetricaHistorial metrica) {
        switch (tipoRecurso.toUpperCase()) {
            case "CPU": return metrica.getUsoCpu();
            case "RAM": return metrica.getUsoRam();
            case "DISCO": return metrica.getUsoDisco();
            default: return 0.0;
        }
    }
    
    private boolean valorSuperaUmbral(String tipoRecurso, MetricaHistorial metrica, Umbral umbral) {
        return getValorPorRecurso(tipoRecurso, metrica) > umbral.getPorcentaje();
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