package com.svit.server_vitals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard() {

        // recuperq el nombre de la plantilla del dashboard.html

        return "dashboard";
    }
}