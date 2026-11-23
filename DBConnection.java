package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = env("DB_URL",
            "jdbc:mysql://localhost:3306/library_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
    private static final String USER = env("DB_USER", "root");
    private static final String PASSWORD = env("DB_PASSWORD", "Vika@2610/");

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
