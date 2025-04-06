package com.svit.server_vitals.repository;

import com.svit.server_vitals.model.Umbral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UmbralRepository extends JpaRepository<Umbral, Long> {
    Optional<Umbral> findByTipoRecurso(String tipoRecurso);
}