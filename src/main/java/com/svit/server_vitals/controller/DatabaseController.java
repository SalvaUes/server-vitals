package com.svit.server_vitals.controller;

import com.svit.server_vitals.service.DatabaseDetectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("databases")
public class DatabaseController {

    @Autowired
    private DatabaseDetectorService service;

    @GetMapping("/status")
    public Map<String, String> getStatuses() {
        return service.getDatabaseStatuses();
    }
}

