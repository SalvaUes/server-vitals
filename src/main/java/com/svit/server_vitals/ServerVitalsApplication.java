package com.svit.server_vitals;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SpringBootApplication
@EnableScheduling 
public class ServerVitalsApplication {

    private static final Logger log = LoggerFactory.getLogger(ServerVitalsApplication.class);

    public static void main(String[] args) {
          log.info("========== INICIANDO Server Vitals ==========");
        try {
        SpringApplication.run(ServerVitalsApplication.class, args);
         log.info(" Server Vitals iniciado correctamente.");
        } catch (Exception e) {
            log.error(" Error al iniciar Server Vitals: ", e);
        }
    }

}