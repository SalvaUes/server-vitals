package com.svit.server_vitals.service;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoreoService {

    private final UmbralRepository umbralRepository;
    private final MailService mailService;

    public MonitoreoService(UmbralRepository umbralRepository, MailService mailService) {
        this.umbralRepository = umbralRepository;
        this.mailService = mailService;
    }

    
    @Scheduled(fixedRate = 10000)
    public void monitorearRecursos() {
        List<Umbral> umbrales = umbralRepository.findAll();

      
        for (Umbral umbral : umbrales) {
           
            double valorRecursoActual = obtenerValorRecurso(umbral.getTipoRecurso());

            
            if (valorRecursoActual > umbral.getValorMaximo()) {
                String mensaje = "Alerta: El valor del recurso " + umbral.getTipoRecurso() +
                        " ha superado el umbral máximo. Valor actual: " + valorRecursoActual;

                
                mailService.enviarCorreoAlerta("alertasdaw@gmail.com", "Alerta de Umbral Superado", mensaje);
            }
        }
    }


    private double obtenerValorRecurso(String tipoRecurso) {
        
        return switch (tipoRecurso) {
            case "CPU" -> 85.0;
            case "RAM" -> 75.0;
            case "DISCO" -> 90.0;
            default -> 0.0;
        };
    }
}
