package com.inventory.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class databaseConfig {
    private static final Properties properties= new Properties();
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (InputStream input=databaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
                if(input == null) {
                    throw new RuntimeException("db.properties not found");
                }
                properties.load(input);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver was not found", e);

        } catch (Exception e) {
            throw new RuntimeException("failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );
    }
}