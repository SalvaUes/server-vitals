package com.svit.server_vitals.service;

import com.svit.server_vitals.model.EventLog;
import com.svit.server_vitals.model.LogLevel;
import com.svit.server_vitals.repository.EventLogRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventLogService {

    @Autowired
    private EventLogRepository eventLogRepository;

    public void log(LogLevel nivel, String mensaje, String detalles, String origen) {
        EventLog log = new EventLog(nivel, mensaje, detalles, origen);
        eventLogRepository.save(log);
    }

    public void logInfo(String mensaje, String origen) {
        log(LogLevel.INFO, mensaje, null, origen);
    }

    public void logError(String mensaje, String detalles, String origen) {
        log(LogLevel.ERROR, mensaje, detalles, origen);
    }

    public void logDebug(String mensaje, String origen) {
        log(LogLevel.DEBUG, mensaje, null, origen);
    }

    public void logWarn(String mensaje, String origen) {
        log(LogLevel.WARN, mensaje, null, origen);
    }
    
    public List<EventLog> obtenerTodosLosLogs() {
        return eventLogRepository.findAllByOrderByFechaRegistroDesc();
    }
}
