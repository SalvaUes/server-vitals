package com.svit.server_vitals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.svit.server_vitals.model.Alerta;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findAllByOrderByFechaEnvioDesc();
}