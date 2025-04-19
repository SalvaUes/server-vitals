package com.svit.server_vitals.service;

import com.svit.server_vitals.model.Umbral;
import com.svit.server_vitals.repository.UmbralRepository;
import org.springframework.beans.factory.annotation.Autowired; // Asegúrate de tener esta importación
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar para manejo de transacciones

import java.util.List;
import java.util.Optional; // Importar Optional si usas findById

@Service
public class UmbralService {

    @Autowired // Inyección del repositorio
    private UmbralRepository repository;

    // No necesitas constructor si usas @Autowired

    public List<Umbral> getAll() {
        return repository.findAll();
    }

    @Transactional // Buena práctica para operaciones de escritura/modificación
    public Umbral save(Umbral umbral) {
        // Puedes añadir validaciones aquí si es necesario antes de guardar
        return repository.save(umbral);
    }

    public Umbral getByTipoRecurso(String tipoRecurso) {
        // findByTipoRecurso devuelve Optional<Umbral>
        return repository.findByTipoRecurso(tipoRecurso).orElse(null);
    }

    // --- MÉTODO AÑADIDO ---
    @Transactional // Buena práctica para operaciones de eliminación
    public void deleteById(Long id) {
        // Verificar si existe antes de eliminar (opcional pero recomendado)
        Optional<Umbral> umbralOptional = repository.findById(id);
        if (umbralOptional.isPresent()) {
            repository.deleteById(id); // Llama al método deleteById del JpaRepository
        } else {
            // Puedes lanzar una excepción personalizada o manejar el caso de no encontrado
            // Por ejemplo: throw new ResourceNotFoundException("Umbral no encontrado con id: " + id);
            System.err.println("Intento de eliminar Umbral con ID no existente: " + id);
        }
    }

    // Método findById si lo necesitas en algún otro lugar
    public Optional<Umbral> findById(Long id) {
        return repository.findById(id);
    }
}
