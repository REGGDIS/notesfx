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
            // índice FTS5
            "CREATE VIRTUAL TABLE IF NOT EXISTS notes_fts USING fts5(" +
                    "title, content, content='notes', content_rowid='id'" +
                    ")",
            // trigger AFTER INSERT
            "CREATE TRIGGER IF NOT EXISTS notes_ai AFTER INSERT ON notes BEGIN\n" +
                    "  INSERT INTO notes_fts(rowid, title, content) VALUES (new.id, new.title, new.content);\n" +
                    "END;",
            // trigger AFTER DELETE
            "CREATE TRIGGER IF NOT EXISTS notes_ad AFTER DELETE ON notes BEGIN\n" +
                    "  INSERT INTO notes_fts(notes_fts, rowid, title, content) VALUES('delete', old.id, old.title, old.content);\n" +
                    "END;",
            // trigger AFTER UPDATE
            "CREATE TRIGGER IF NOT EXISTS notes_au AFTER UPDATE ON notes BEGIN\n" +
                    "  INSERT INTO notes_fts(notes_fts, rowid, title, content) VALUES('delete', old.id, old.title, old.content);\n" +
                    "  INSERT INTO notes_fts(rowid, title, content) VALUES (new.id, new.title, new.content);\n" +
                    "END;"
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
