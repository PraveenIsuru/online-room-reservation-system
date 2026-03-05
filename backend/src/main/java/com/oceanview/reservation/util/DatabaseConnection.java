package com.oceanview.reservation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private static final String PROPS_FILE = "/db.properties";

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream(PROPS_FILE));
            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Reinitialise if connection dropped
                instance = null;
                return getInstance().getConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection check failed", e);
        }
        return connection;
    }
}
