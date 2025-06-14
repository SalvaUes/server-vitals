package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.service.UmbralService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; 
import java.util.List; 

@Controller
@RequestMapping("/umbrales")
public class UmbralController {

    @Autowired 
    private UmbralService umbralService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("umbral", new Umbral()); 
        List<Umbral> listaUmbrales = umbralService.getAll();
        model.addAttribute("listaUmbrales", listaUmbrales);
        
        
        model.addAttribute("activePage", "umbrales"); 

        return "umbral"; 
    }

    @PostMapping("/guardar")
    public String guardarUmbral(@ModelAttribute("umbral") Umbral umbral) {
        try {
            umbral.setFechaConfiguracion(java.time.LocalDateTime.now());
            umbralService.save(umbral);
            return "redirect:/umbrales?exito"; 
        } catch (Exception e) {
            e.printStackTrace(); 
            return "redirect:/umbrales?error";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminarUmbral(@PathVariable Long id) {
        try {
            umbralService.deleteById(id); 
            return "redirect:/umbrales?eliminado";
        } catch (Exception e) {
            return "redirect:/umbrales?errorEliminar";
        }
    }
}