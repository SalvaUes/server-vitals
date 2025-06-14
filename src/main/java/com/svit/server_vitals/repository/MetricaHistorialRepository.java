    package com.svit.server_vitals.repository;

    import com.svit.server_vitals.model.MetricaHistorial;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface MetricaHistorialRepository extends JpaRepository<MetricaHistorial, Long> {
    }
    