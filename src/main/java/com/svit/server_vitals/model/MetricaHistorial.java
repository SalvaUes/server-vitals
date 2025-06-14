package com.svit.server_vitals.model;

    import jakarta.persistence.Entity;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import java.time.LocalDateTime;

    @Entity
    public class MetricaHistorial {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private LocalDateTime fechaHora;

        private double usoCpu;
        private double usoRam;
        private double usoDisco;

        @Enumerated(EnumType.STRING) 
        private NivelAlerta nivelAlerta;

        

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
        }

        public double getUsoCpu() {
            return usoCpu;
        }

        public void setUsoCpu(double usoCpu) {
            this.usoCpu = usoCpu;
        }

        public double getUsoRam() {
            return usoRam;
        }

        public void setUsoRam(double usoRam) {
            this.usoRam = usoRam;
        }

        public double getUsoDisco() {
            return usoDisco;
        }

        public void setUsoDisco(double usoDisco) {
            this.usoDisco = usoDisco;
        }

        public NivelAlerta getNivelAlerta() {
            return nivelAlerta;
        }

        public void setNivelAlerta(NivelAlerta nivelAlerta) {
            this.nivelAlerta = nivelAlerta;
        }
    }