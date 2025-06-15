package com.svit.server_vitals.repository;

import com.svit.server_vitals.model.ConfiguracionNotificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionNotificacionesRepository extends JpaRepository<ConfiguracionNotificaciones, Long> {
    
}