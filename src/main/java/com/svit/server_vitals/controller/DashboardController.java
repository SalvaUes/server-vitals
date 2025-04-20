package com.svit.server_vitals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // Importar Model si se necesita pasar datos

@Controller
public class DashboardController {


    @GetMapping("/")
    public String redirectToDashboard() {
        // Redirige automáticamente del path raíz "/" al path "/dashboard"
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        // Aquí puedes añadir lógica si necesitas pasar datos al dashboard
        // model.addAttribute("algunaVariable", "algunValor");
        return "dashboard"; // Devuelve el nombre de la plantilla dashboard.html
    }
}
