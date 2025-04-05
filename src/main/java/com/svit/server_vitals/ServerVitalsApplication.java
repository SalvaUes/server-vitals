package com.svit.server_vitals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication
@EnableScheduling 
public class ServerVitalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerVitalsApplication.class, args);
    }

}