package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Alerta;
import com.svit.server_vitals.repository.AlertaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 


@Controller
@RequestMapping("/correo")
public class AlertaController {

    private static final Logger log = LoggerFactory.getLogger(AlertaController.class);

    @Autowired
    private AlertaRepository alertaRepository;

    @GetMapping
    public String mostrarPaginaAlertas(Model model) {
        
        
        if (!model.containsAttribute("nuevaAlerta")) {
            model.addAttribute("nuevaAlerta", new Alerta());
        }
       
        
        model.addAttribute("listaAlertas", alertaRepository.findAll());
        
        
        model.addAttribute("activePage", "correo");
        return "correo";
    }

    
    
    @PostMapping("/guardar")
    public String guardarAlerta(@ModelAttribute("nuevaAlerta") Alerta alerta, RedirectAttributes redirectAttributes) {
        try {
            alertaRepository.save(alerta);
            log.info("Alerta guardada exitosamente: {}", alerta);
            redirectAttributes.addFlashAttribute("exito_guardado", "¡Alerta guardada exitosamente!");
        } catch (Exception e) {
            log.error("Error al guardar la alerta: {}", e.getMessage());
            
            redirectAttributes.addFlashAttribute("error_guardado", "Error al guardar la alerta. Revisa los logs.");
            redirectAttributes.addFlashAttribute("nuevaAlerta", alerta);
        }
        return "redirect:/correo";
    }
   

    @PostMapping("/eliminar/{id}")
    public String eliminarAlerta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alertaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("exito_eliminado", "Alerta eliminada correctamente.");
        } catch (Exception e) {
            log.error("Error al eliminar la alerta con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error_guardado", "Error al eliminar la alerta.");
        }
        return "redirect:/correo";
    }
}