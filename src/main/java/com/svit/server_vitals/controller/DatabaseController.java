package com.svit.server_vitals.controller;

import com.svit.server_vitals.service.DatabaseDetectorService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Map;


public class DatabaseController {

    @Autowired
    private DatabaseDetectorService service;

    
    public Map<String, String> getStatuses() {
        return service.getDatabaseStatuses();
    }
}

