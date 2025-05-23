package com.svit.server_vitals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.svit.server_vitals.model.Alerta;
import java.util.List; 

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    
    List<Alerta> findByTipoRecurso(String tipoRecurso);

}
