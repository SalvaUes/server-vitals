package com.svit.server_vitals.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;



@Entity
public class ConfiguracionNotificaciones {

    @Id
    private Long id; 
    
    @Column(nullable = false)
    private Integer intervaloMinutos; 
    

    private LocalDateTime ultimaNotificacionEnviada; 
    

    
    
    public ConfiguracionNotificaciones() {
    }

    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(Integer intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public LocalDateTime getUltimaNotificacionEnviada() {
        return ultimaNotificacionEnviada;
    }

    public void setUltimaNotificacionEnviada(LocalDateTime ultimaNotificacionEnviada) {
        this.ultimaNotificacionEnviada = ultimaNotificacionEnviada;
    }
}