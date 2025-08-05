// Crea tablas, FTS5 y triggers
package com.regdevs.notesfx.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {
    private static final String[] SCHEMA = {
            "PRAGMA journal_mode=WAL",
            // tabla notes
            """
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL DEFAULT '',
                content TEXT NOT NULL DEFAULT '',
                tags TEXT NOT NULL DEFAULT '',
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
            """,
            // FTS5 y triggers
    };

    public static void init(Connection conn) {
        try (Statement st = conn.createStatement()) {
            for (String sql : SCHEMA) {
                st.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando esquema", e);
        }
    }
}
