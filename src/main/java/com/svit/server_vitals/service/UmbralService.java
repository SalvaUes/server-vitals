package com.svit.server_vitals.service;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UmbralService {

    @Autowired 
    private UmbralRepository repository;

    

    public List<Umbral> getAll() {
        return repository.findAll();
    }

    @Transactional 
    public Umbral save(Umbral umbral) {
        
        return repository.save(umbral);
    }

    public Umbral getByTipoRecurso(String tipoRecurso) {
        
        return repository.findByTipoRecurso(tipoRecurso).orElse(null);
    }

    
    @Transactional 
    public void deleteById(Long id) {
        
        Optional<Umbral> umbralOptional = repository.findById(id);
        if (umbralOptional.isPresent()) {
            repository.deleteById(id); 
        } else {
            
            System.err.println("Intento de eliminar Umbral con ID no existente: " + id);
        }
    }

    
    public Optional<Umbral> findById(Long id) {
        return repository.findById(id);
    }
}
