package com.svit.server_vitals.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Umbral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoRecurso;

    @Column(nullable = false)
    private Double porcentaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivelAlerta;

    private LocalDateTime fechaConfiguracion;

    // Constructor por defecto
    public Umbral() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(String tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public NivelAlerta getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(NivelAlerta nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    public LocalDateTime getFechaConfiguracion() {
        return fechaConfiguracion;
    }

    public void setFechaConfiguracion(LocalDateTime fechaConfiguracion) {
        this.fechaConfiguracion = fechaConfiguracion;
    }
}