package com.regdevs.notesfx.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static Connection conn;

    public static synchronized Connection get() {
        if (conn == null) {
            try {
                // Carpeta .notesfx en el home
                Path dir = Paths.get(System.getProperty("user.home"), ".notesfx");
                Files.createDirectories(dir);

                // Archivo notes.db dentro de esa carpeta
                String url = "jdbc:sqlite:" + dir.resolve("notes.db");
                conn = DriverManager.getConnection(url);
            } catch (Exception e) {
                throw new RuntimeException("Error inicializando la base de datos", e);
            }
        }
        return conn;
    }
}
