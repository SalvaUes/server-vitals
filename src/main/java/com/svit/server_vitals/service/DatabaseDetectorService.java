package com.svit.server_vitals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DatabaseDetectorService {

    @Value("${postgresql.url}")
    private String pgUrl;
    @Value("${postgresql.username}")
    private String pgUser;
    @Value("${postgresql.password}")
    private String pgPass;

    @Value("${mysql.url}")
    private String mysqlUrl;
    @Value("${mysql.username}")
    private String mysqlUser;
    @Value("${mysql.password}")
    private String mysqlPass;

    @Value("${oracle.url}")
    private String oracleUrl;
    @Value("${oracle.username}")
    private String oracleUser;
    @Value("${oracle.password}")
    private String oraclePass;

    private static final Logger log = LoggerFactory.getLogger(DatabaseDetectorService.class);
    /**
     * Últimos resultados de salud de bases.
     */
    private Map<String, DatabaseStatus> lastStatuses = new LinkedHashMap<>();

    public Map<String, DatabaseStatus> getDatabaseStatuses() {
        return lastStatuses;
    }

    /**
     * Se ejecuta cada 30 segundos (puedes ajustar el cron o fixedRate).
     */
    @Scheduled(fixedRateString = "${dbmonitor.rate.millis:30000}")
    public void checkAllDatabases() {
        Map<String, DatabaseStatus> statuses = new LinkedHashMap<>();

        statuses.put("PostgreSQL", checkJdbc(() -> checkJdbcConn(pgUrl, pgUser, pgPass)));
        statuses.put("MySQL",      checkJdbc(() -> checkJdbcConn(mysqlUrl, mysqlUser, mysqlPass)));
        statuses.put("Oracle",     checkJdbc(() -> checkJdbcConn(oracleUrl, oracleUser, oraclePass)));
       log.info("► Iniciando health check de bases de datos");
        // … aquí tu lógica de consulta y medición …
        log.info("► Health check finalizado, estados: {}", lastStatuses);

        // Guardamos para exponer vía endpoint o dashboard
        this.lastStatuses = statuses;
    }

    private DatabaseStatus checkJdbcConn(String url, String user, String pass) {
        long start = System.currentTimeMillis();
        boolean ok;
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            ok = conn != null && !conn.isClosed();
        } catch (SQLException e) {
            ok = false;
        }
        long took = System.currentTimeMillis() - start;
        return new DatabaseStatus(ok, took);
    }



    /**
     * Wrapper genérico para medir tiempo y estandarizar.
     */
    private DatabaseStatus checkJdbc(CheckAction action) {
        try {
            return action.execute();
        } catch (Exception e) {
            return new DatabaseStatus(false, -1);
        }
    }
    @FunctionalInterface
    private interface CheckAction {
        DatabaseStatus execute() throws Exception;
    }

    /**
     * POJO simple para llevar estado + tiempo.
     */
    public static class DatabaseStatus {
        private final boolean active;
        private final long responseTimeMs;

        public DatabaseStatus(boolean active, long responseTimeMs) {
            this.active = active;
            this.responseTimeMs = responseTimeMs;
        }
        public boolean isActive() { return active; }
        public long getResponseTimeMs() { return responseTimeMs; }
    }
}
