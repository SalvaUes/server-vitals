package com.svit.server_vitals.service;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.svit.server_vitals.model.LogLevel;

import java.util.List;
import java.util.Optional;

@Service
public class UmbralService {


   
    private final UmbralRepository repository;
    private final EventLogService eventLogService;

    
    public UmbralService(UmbralRepository repository, EventLogService eventLogService) {
        this.repository = repository;
        this.eventLogService = eventLogService;
    }

    public List<Umbral> getAll() {
        return repository.findAll();
    }

    @Transactional 
    public Umbral save(Umbral umbral) { 
      try {
        Umbral saved = repository.save(umbral);

        if (saved == null || saved.getId() == null) {
            eventLogService.log(
                LogLevel.ERROR,
                String.format("Error al guardar Umbral para recurso: %s", umbral.getTipoRecurso()),
                "Resultado: null devuelto por repository.save()",
                "Origen: UmbralService.save"
            );
            return null; 
        }

        eventLogService.log(
            LogLevel.INFO,
            String.format("Umbral guardado para recurso: %s con nivel %s y porcentaje %.2f%%",
                saved.getTipoRecurso(),
                saved.getNivelAlerta(),
                saved.getPorcentaje()
            ),
            "ID: " + saved.getId(),
            "Origen: UmbralService.save"
        );
        return saved;

   } catch (Exception e) {
    eventLogService.log(
        LogLevel.ERROR,
        String.format("Excepción al guardar Umbral para recurso: %s", umbral.getTipoRecurso()),
        e.getMessage(),
        "Origen: UmbralService.save"
    );
    throw e;
}

    }

    public Umbral getByTipoRecurso(String tipoRecurso) {
       Optional<Umbral> resultado = repository.findByTipoRecurso(tipoRecurso);
    if (resultado.isEmpty()) {
        eventLogService.log(
            LogLevel.WARN,
            String.format("No se encontró umbral para recurso: %s", tipoRecurso),
            "Resultado: null",
            "Origen: UmbralService.getByTipoRecurso"
        );
    }
    return resultado.orElse(null);
    }

    
    @Transactional 
    public void deleteById(Long id) {
        
        Optional<Umbral> umbralOptional = repository.findById(id);
        if (umbralOptional.isPresent()) {
            repository.deleteById(id); 
        } else {
             eventLogService.log(
                LogLevel.ERROR,
                String.format("Intento de eliminar Umbral con ID no existente: %d", id),
                "Resultado: No encontrado",
                "Origen: UmbralService.deleteById"
            );
        }
    }

    
    public Optional<Umbral> findById(Long id) {
        return repository.findById(id);
    }
}
