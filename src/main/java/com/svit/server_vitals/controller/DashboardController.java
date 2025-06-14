package com.svit.server_vitals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        
        
        model.addAttribute("activePage", "dashboard");
        
        
        model.addAttribute("includeDashboardScript", true);

        return "dashboard"; 
    }
}