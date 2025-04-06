package com.svit.server_vitals.service;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmbralService {

    private final UmbralRepository repository;

    public UmbralService(UmbralRepository repository) {
        this.repository = repository;
    }

    public List<Umbral> getAll() {
        return repository.findAll();
    }

    public Umbral save(Umbral umbral) {
        return repository.save(umbral);
    }

    public Umbral getByTipoRecurso(String tipoRecurso) {
        return repository.findByTipoRecurso(tipoRecurso).orElse(null);
    }
}