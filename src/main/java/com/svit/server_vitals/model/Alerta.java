package com.svit.server_vitals.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String correoDestino;

    @Column(nullable = false)
    private String tipoRecurso;

    private String mensaje;

    
    @Column(nullable = false)
    private Integer intervaloMinutos;

    private LocalDateTime ultimaNotificacionEnviada;
    
    

    
   
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCorreoDestino() { return correoDestino; }
    public void setCorreoDestino(String correoDestino) { this.correoDestino = correoDestino; }
    public String getTipoRecurso() { return tipoRecurso; }
    public void setTipoRecurso(String tipoRecurso) { this.tipoRecurso = tipoRecurso; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Integer getIntervaloMinutos() { return intervaloMinutos; }
    public void setIntervaloMinutos(Integer intervaloMinutos) { this.intervaloMinutos = intervaloMinutos; }
    public LocalDateTime getUltimaNotificacionEnviada() { return ultimaNotificacionEnviada; }
    public void setUltimaNotificacionEnviada(LocalDateTime ultimaNotificacionEnviada) { this.ultimaNotificacionEnviada = ultimaNotificacionEnviada; }
}