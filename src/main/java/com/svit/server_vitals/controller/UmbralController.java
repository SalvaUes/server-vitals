package com.svit.server_vitals.controller;

import com.svit.server_vitals.dto.UmbralDTO;
import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.service.UmbralService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/umbrales")
public class UmbralController {

    private final UmbralService umbralService;

    public UmbralController(UmbralService umbralService) {
        this.umbralService = umbralService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("umbral", new Umbral());
        model.addAttribute("listaUmbrales", umbralService.getAll());
        return "umbral";
    }

    @PostMapping("/guardar")
    public String guardarUmbral(@ModelAttribute Umbral umbral) {
        umbral.setFechaConfiguracion(LocalDateTime.now());
        umbralService.save(umbral);
        return "redirect:/umbrales";
    }

    
    @PostMapping
    @ResponseBody
    public ResponseEntity<String> guardarUmbralAjax(@RequestBody UmbralDTO umbral) {
        Umbral nuevo = new Umbral();
        nuevo.setTipoRecurso(umbral.getTipo());
        nuevo.setValorMaximo(umbral.getValor());
        nuevo.setUnidad(umbral.getUnidad());
        nuevo.setFechaConfiguracion(LocalDateTime.now());

        umbralService.save(nuevo);
        return ResponseEntity.ok("Umbral guardado exitosamente");
    }
}
