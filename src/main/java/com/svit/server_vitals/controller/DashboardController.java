package com.svit.server_vitals.controller;

import com.svit.server_vitals.service.MetricaHistorialPdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard") // <<---- IMPORTANTE: debe existir este nivel
public class DashboardController {

    @Autowired
    private MetricaHistorialPdfService metricaHistorialPdfService;

    @GetMapping
    public String dashboardView() {
        return "dashboard"; // debe estar en src/main/resources/templates/dashboard.html
    }

    @GetMapping("/pdf")
    public void generarPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"metricas_historial.pdf\"");
        metricaHistorialPdfService.generarReportePdf(response.getOutputStream());
    }
}
