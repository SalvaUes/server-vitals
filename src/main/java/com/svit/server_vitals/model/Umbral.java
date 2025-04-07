package com.svit.server_vitals.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Umbral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipoRecurso")
    private String tipoRecurso; 
    @Column(name = "valorMaximo")
    private double valorMaximo;
    @Column(name = "unidad")
    private String unidad;
    @Column(name = "valorMinimo")
    private double valorMinimo;
    @Column(name = "fechaConfiguracion")
    private LocalDateTime fechaConfiguracion;

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
    public double getValorMaximo() {
        return valorMaximo;
    }
    public void setValorMaximo(double valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public String getUnidad() {
        return unidad;
    }
    
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getValorMinimo() {
        return valorMinimo;
    }
    public void setValorMinimo(double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }
    public LocalDateTime getFechaConfiguracion() {
        return fechaConfiguracion;
    }
    public void setFechaConfiguracion(LocalDateTime fechaConfiguracion) {
        this.fechaConfiguracion = fechaConfiguracion;
    }
    public void setUnidad(double unidad2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setUnidad'");
    }


    
}
