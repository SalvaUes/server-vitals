package com.svit.server_vitals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.svit.server_vitals.model.EventLog;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
List<EventLog> findAllByOrderByFechaRegistroDesc();


}