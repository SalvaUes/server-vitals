package com.svit.server_vitals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; 

@Controller
public class DashboardController {


    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return "dashboard"; 
    }
}
