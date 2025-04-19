package com.svit.server_vitals.controller;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.service.UmbralService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; // Asegúrate de tener esta importación
import java.util.List; // Asegúrate de tener esta importación

@Controller
@RequestMapping("/umbrales")
public class UmbralController {

    @Autowired // Usar Autowired para inyección
    private UmbralService umbralService;

    // No necesitas un constructor si usas @Autowired en el campo

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("umbral", new Umbral()); // Objeto para el formulario
        List<Umbral> listaUmbrales = umbralService.getAll();
        model.addAttribute("listaUmbrales", listaUmbrales); // Lista para la tabla
        return "umbral"; // Nombre de la plantilla HTML (umbral.html)
    }

    @PostMapping("/guardar")
    public String guardarUmbral(@ModelAttribute("umbral") Umbral umbral) { // Recibe el objeto del formulario
        try {
            umbral.setFechaConfiguracion(java.time.LocalDateTime.now());
            umbralService.save(umbral);
            return "redirect:/umbrales?exito"; // Redirige con parámetro de éxito
        } catch (Exception e) {
            // Manejo básico de errores, podrías añadir un parámetro de error
            return "redirect:/umbrales?error";
        }
    }

    // Añadir método para eliminar si es necesario (no estaba en tu HTML original pero sí en la tabla simulada)
    @PostMapping("/eliminar/{id}")
    public String eliminarUmbral(@PathVariable Long id) {
        try {
            // Asumiendo que tienes un método deleteById en tu servicio/repositorio
            umbralService.deleteById(id); // Necesitarás añadir este método a UmbralService y UmbralRepository
            return "redirect:/umbrales?eliminado";
        } catch (Exception e) {
            return "redirect:/umbrales?errorEliminar";
        }
    }
}
