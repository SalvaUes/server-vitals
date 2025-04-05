package com.svit.server_vitals.controller;

import com.svit.server_vitals.dto.SystemResourceDto;
import com.svit.server_vitals.service.SystemMonitorService; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.MediaType; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter; 



import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit; 

@RestController
public class SystemResourceController {

    private static final Logger log = LoggerFactory.getLogger(SystemResourceController.class);

    // inyeccion dek servicio

    @Autowired
    private SystemMonitorService systemMonitorService;

    
    @GetMapping("/api/system/resources")
    public ResponseEntity<SystemResourceDto> getSystemResourcesPolling() {
        log.debug("Polling request received for /api/system/resources");
        SystemResourceDto latestMetrics = systemMonitorService.getLatestMetrics();
        return ResponseEntity.ok(latestMetrics);
    }

    // ENDPOINT

    private final ExecutorService sseExecutor = Executors.newCachedThreadPool(r -> {
         Thread t = new Thread(r);
         t.setName("sse-emitter-thread-" + t.getId()); 
         t.setDaemon(true); 
         return t;
     });

    
    @GetMapping(value = "/api/system/resources-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSystemResources() {
        log.info("SSE connection requested. Creating emitter.");

        
        SseEmitter emitter = new SseEmitter(0L);

        
        
        sseExecutor.execute(() -> {
            try {
                log.debug("SSE emitter task started for client.");
                
                SystemResourceDto initialMetrics = systemMonitorService.getLatestMetrics();
                emitter.send(SseEmitter.event().name("system-update").id(String.valueOf(System.currentTimeMillis())).data(initialMetrics));
                log.debug("Sent initial SSE data.");

                // enviar actualizaciones

                while (true) { 
                    TimeUnit.MILLISECONDS.sleep(1000); // Espera 1 segundos

                    SystemResourceDto latestMetrics = systemMonitorService.getLatestMetrics();

                    
                    emitter.send(SseEmitter.event().name("system-update").id(String.valueOf(System.currentTimeMillis())).data(latestMetrics));
                    log.trace("Sent periodic SSE data."); 
                }
            } catch (IOException e) {
                
                log.info("SSE connection closed or I/O error for client: {}", e.getMessage());
                
            } catch (InterruptedException e) {
                 log.warn("SSE emitter task interrupted.");
                 Thread.currentThread().interrupt(); 
                 emitter.completeWithError(e); 
            } catch (Exception e) {
                 
                 log.error("Unexpected error in SSE stream task: {}", e.getMessage(), e);
                 emitter.completeWithError(e); 
            } finally {
                 
                 log.debug("Completing SSE emitter for client.");
                 emitter.complete();
            }
        });

        log.info("Returning SSE emitter to client.");
        return emitter; 
    }
}