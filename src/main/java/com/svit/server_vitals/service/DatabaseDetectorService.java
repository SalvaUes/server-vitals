package com.svit.server_vitals.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DatabaseDetectorService {

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
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://java_db:5432/postgres", "postgres", "postgres")) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkMySQL() {
        try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkOracle() {
        try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUsername, oraclePassword)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
