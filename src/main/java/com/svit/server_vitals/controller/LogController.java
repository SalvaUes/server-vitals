package com.svit.server_vitals.controller;

import com.svit.server_vitals.service.EventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogController {

    @Autowired
    private EventLogService service;

    @GetMapping("/logs")
    public String mostrarLogs(Model model) {
        model.addAttribute("logs", service.obtenerTodosLosLogs());
         model.addAttribute("activePage", "logs");
        return "logs";
    }
}
