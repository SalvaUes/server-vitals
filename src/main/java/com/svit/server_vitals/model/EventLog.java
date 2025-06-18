package com.svit.server_vitals.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel nivel;

    @Column(nullable = false)
    private String mensaje;

    @Column(length = 4000)
    private String detalles;

    private String origen;

    private LocalDateTime fechaRegistro;

    public EventLog() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public EventLog(LogLevel nivel, String mensaje, String detalles, String origen) {
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.detalles = detalles;
        this.origen = origen;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public LogLevel getNivel() {
        return nivel;
    }

    public void setNivel(LogLevel nivel) {
        this.nivel = nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}