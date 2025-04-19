package com.svit.server_vitals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.svit.server_vitals.model.Alerta;
import java.util.List; // Asegúrate de importar List

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Añadir este método si no existe:
    List<Alerta> findByTipoRecurso(String tipoRecurso);

}
