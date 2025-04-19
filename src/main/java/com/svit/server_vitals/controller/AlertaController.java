package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.repository.UmbralRepository;
import com.svit.server_vitals.service.MailService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AlertaController {

    private static final Logger log = LoggerFactory.getLogger(AlertaController.class);

    @Autowired
    private UmbralRepository umbralRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private MailService mailService;

    @GetMapping("/correo")
    public String mostrarFormularioCorreo(Model model) {
        log.info("Accediendo a GET /correo");
        List<Umbral> listaUmbrales = umbralRepository.findAll();
        List<Alerta> listaAlertas = alertaRepository.findAll();
        log.info("Número de umbrales encontrados: {}", listaUmbrales.size());
        log.info("Número de alertas encontradas: {}", listaAlertas.size());

        model.addAttribute("listaUmbrales", listaUmbrales);
        model.addAttribute("listaAlertas", listaAlertas);
        model.addAttribute("alerta", new Alerta());

        return "correo";
    }

    @PostMapping("/enviar-correo")
    public String guardarAlerta(
            @RequestParam String destinatario,
            @RequestParam String tipoRecurso,
            @RequestParam String contenido) {

        log.info("Intentando guardar alerta con datos:");
        log.info("Destinatario: {}", destinatario);
        log.info("Tipo Recurso: {}", tipoRecurso);
        log.info("Contenido: {}", contenido);

        Alerta nuevaAlerta = new Alerta();
        nuevaAlerta.setCorreoDestino(destinatario);
        nuevaAlerta.setTipoRecurso(tipoRecurso);
        nuevaAlerta.setMensaje(contenido);

        try {
            log.info("Llamando a alertaRepository.save()...");
            alertaRepository.save(nuevaAlerta);
            log.info("Alerta guardada exitosamente con ID: {}", nuevaAlerta.getId());

            log.info("Intentando enviar correo de confirmación...");
            String asunto = "Prueba de Configuracion SVITS";
            String mensaje = "Se configuro una alerta para " + tipoRecurso;
            if (mailService != null) {
                 mailService.enviarCorreoAlerta(destinatario, asunto, mensaje);
                 log.info("Llamada a enviarCorreoAlerta completada (revisar logs de MailService para éxito/error).");
            } else {
                 log.warn("MailService no está inyectado, no se enviará correo de confirmación.");
            }

            return "redirect:/correo?guardado";

        } catch (Exception e) {
            log.error("¡ERROR al guardar la alerta en la base de datos o al enviar correo!", e);
            return "redirect:/correo?errorGuardar";
        }
    }

    @PostMapping("/alerta/eliminar/{id}")
    public String eliminarAlerta(@PathVariable Long id) {
        log.info("Intentando eliminar alerta con ID: {}", id);
        try {
            alertaRepository.deleteById(id);
            log.info("Alerta con ID: {} eliminada exitosamente.", id);
            return "redirect:/correo?eliminado";
        } catch (Exception e) {
            log.error("Error al eliminar la alerta con ID: {}", id, e);
            return "redirect:/correo?errorEliminar";
        }
    }
}
