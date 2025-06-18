package com.svit.server_vitals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;


public class DatabaseDetectorService {
private static final Logger log = LoggerFactory.getLogger(DatabaseDetectorService.class);

    @Value("${mysql.url}")
    private String mysqlUrl;
    @Value("${mysql.username}")
    private String mysqlUsername;
    @Value("${mysql.password}")
    private String mysqlPassword;

    @Value("${oracle.url}")
    private String oracleUrl;
    @Value("${oracle.username}")
    private String oracleUsername;
    @Value("${oracle.password}")
    private String oraclePassword;

    public Map<String, String> getDatabaseStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        
        statuses.put("PostgreSQL", checkPostgres() ? "Operativo" : "No disponible");
        statuses.put("MySQL", checkMySQL() ? "Operativo" : "No disponible");
        statuses.put("Oracle", checkOracle() ? "Operativo" : "No disponible");
        return statuses;
    }

    private boolean checkPostgres() {
         log.debug("Intentando conexión a PostgreSQL...");
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://java_db:5432/postgres", "postgres", "postgres")) {
             boolean status = conn != null && !conn.isClosed();
          log.debug("Conexión a PostgreSQL {}", status ? "exitosa" : "fallida");
        return status;
    } catch (SQLException e) {
        log.warn("Error conectando a PostgreSQL: {}", e.getMessage());
        return false;
       }
    }

    private boolean checkMySQL() {
    log.debug("Intentando conexión a MySQL...");
    try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
        boolean status = conn != null && !conn.isClosed();
        log.debug("Conexión a MySQL {}", status ? "exitosa" : "fallida");
        return status;
    } catch (SQLException e) {
        log.warn("Error conectando a MySQL: {}", e.getMessage());
        return false;
    }
}

    private boolean checkOracle() {
    log.debug("Intentando conexión a Oracle...");
    try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUsername, oraclePassword)) {
        boolean status = conn != null && !conn.isClosed();
        log.debug("Conexión a Oracle {}", status ? "exitosa" : "fallida");
        return status;
    } catch (SQLException e) {
        log.warn("Error conectando a Oracle: {}", e.getMessage());
        return false;
    }

}

}
