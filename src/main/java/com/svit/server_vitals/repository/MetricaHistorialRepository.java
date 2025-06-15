package com.svit.server_vitals.repository;

import com.svit.server_vitals.model.MetricaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricaHistorialRepository extends JpaRepository<MetricaHistorial, Long> {

    
    
    List<MetricaHistorial> findByFechaHoraAfter(LocalDateTime fecha);

}