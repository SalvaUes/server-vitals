package com.svit.server_vitals.controller;

import com.svit.server_vitals.service.DatabaseDetectorService;
import com.svit.server_vitals.service.DatabaseDetectorService.DatabaseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/databases")    // Ruta base para todos los endpoints de BD
public class DatabaseController {

    private final DatabaseDetectorService service;

    @Autowired
    public DatabaseController(DatabaseDetectorService service) {
        this.service = service;
    }

    /**
     * GET /api/databases/status
     * Devuelve un JSON con el estado y tiempo de respuesta de cada BD.
     */
    @GetMapping("/status")
    public Map<String, DatabaseStatus> getStatuses() {
        return service.getDatabaseStatuses();
    }
}

