package com.svit.server_vitals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.service.MailService;

@Controller
@RequestMapping("/correo")
public class AlertaController {

    private final AlertaRepository alertaRepository;
    private final MailService mailService;

    public AlertaController(AlertaRepository alertaRepository, MailService mailService) {
        this.alertaRepository = alertaRepository;
        this.mailService = mailService;
    }

    @GetMapping
    public String listarAlertas(Model model) {
        model.addAttribute("alertas", alertaRepository.findAll()); 
        return "correo"; 
    }

    @PostMapping("/guardar")
    public String guardarUmbral(Alerta alerta, RedirectAttributes redirectAttributes) {
        alertaRepository.save(alerta); 
    
       
        String destinatario = alerta.getCorreoDestino();  
        String asunto = "🚨 Alerta de umbral superado: " + alerta.getTipoRecurso();
        String mensaje = String.format("""
                                       Se ha detectado una alerta en el servidor.
                                       
                                       Recurso: %s
                                       Valor detectado: %.2f
                                       Fecha: %s
                                       
                                       Este correo fue generado autom\u00e1ticamente por Server Vitals.""",
            alerta.getTipoRecurso(),
            alerta.getValorDetectado(),
            alerta.getFechaEnvio() != null ? alerta.getFechaEnvio().toString() : "Fecha no disponible"
        );
    
        // Enviar el correo
        mailService.enviarCorreoAlerta(destinatario, asunto, mensaje);
    
        redirectAttributes.addFlashAttribute("mensaje", "¡Umbral guardado y alerta enviada!");
        return "redirect:/correo";
    }

}


    