package com.oceanview.reservation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private String url;
    private String username;
    private String password;

    private static final String PROPS_FILE = "/db.properties";

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream(PROPS_FILE));
            Class.forName(props.getProperty("db.driver"));
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise database configuration", e);
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
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database connection", e);
        }
    }
}
