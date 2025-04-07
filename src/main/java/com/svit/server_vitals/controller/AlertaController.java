package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.service.UmbralService;
import com.svit.server_vitals.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class AlertaController {

    private final UmbralService umbralService;
    private final MailService mailService;

    public AlertaController(UmbralService umbralService, MailService mailService) {
        this.umbralService = umbralService;
        this.mailService = mailService;
    }

    // Método GET para cargar la página con los umbrales disponibles
    @GetMapping("/correo")
    public String mostrarCorreoFormulario(Model model) {
        model.addAttribute("listaUmbrales", umbralService.getAll());  // Pasar todos los umbrales
        return "correo";  // Redirige a correo.html
    }

    // Método POST para manejar el envío de correo
    @PostMapping("/enviar-correo")
    public String enviarCorreo(@RequestParam("tipoRecurso") String tipoRecurso,
                               @RequestParam("destinatario") String destinatario,
                               @RequestParam("asunto") String asunto,
                               @RequestParam("contenido") String contenido,
                               Model model) {
        // Buscar el umbral por tipo de recurso
        Umbral umbral = umbralService.getByTipoRecurso(tipoRecurso);

        if (umbral != null) {
            // Enviar el correo con los datos del umbral
            String mensajeCorreo = contenido + "\n\n" +
                    "Detalles del Umbral:\n" +
                    "Recurso: " + umbral.getTipoRecurso() + "\n" +
                    "Valor Umbral: " + umbral.getValorMaximo();

            mailService.enviarCorreoAlerta(destinatario, asunto, mensajeCorreo);

            // Pasar mensaje de éxito a la vista
            model.addAttribute("mensaje", "Correo enviado correctamente a " + destinatario);
        } else {
            // Si el umbral no existe
            model.addAttribute("error", "No se encontró el umbral para el recurso: " + tipoRecurso);
        }

        // Volver a cargar los umbrales y pasar a la vista
        model.addAttribute("listaUmbrales", umbralService.getAll());
        return "correo";  // Redirige nuevamente a la página del formulario
    }
}
