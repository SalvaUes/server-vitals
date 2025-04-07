package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.AlertaRepository;
import com.svit.server_vitals.repository.UmbralRepository;
import com.svit.server_vitals.service.UmbralService;
import com.svit.server_vitals.service.MailService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class AlertaController {

     @Autowired
    private UmbralRepository umbralRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @GetMapping("/correo")
    public String mostrarFormularioCorreo(Model model) {
        List<Umbral> listaUmbrales = umbralRepository.findAll();
        List<Alerta> listaAlertas = alertaRepository.findAll();
    
        model.addAttribute("listaUmbrales", listaUmbrales);
        model.addAttribute("listaAlertas", listaAlertas);
    
        return "correo"; // nombre de tu plantilla .html
    }

    @PostMapping("/enviar-correo")
    public String guardarAlerta(
            @RequestParam String destinatario,
            @RequestParam String tipoRecurso,
            @RequestParam String contenido) {

        Alerta alerta = new Alerta();
        alerta.setCorreoDestino(destinatario);
        alerta.setTipoRecurso(tipoRecurso);
        alerta.setMensaje(contenido);

        alertaRepository.save(alerta);

        return "redirect:/correo?guardado";
    }
}
