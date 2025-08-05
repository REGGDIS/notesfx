// Lógica alta de CRUD/FTS
package com.regdevs.notesfx.storage;

import com.regdevs.notesfx.model.Note;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

public class NoteRepository {
    private static NoteRepository INSTANCE;
    private final SqlExecutor sql;
    private final Connection conn;

    private NoteRepository() {
        this.conn = Database.get();
        SchemaInitializer.init(conn);
        this.sql = new SqlExecutor(conn);
    }

    public static NoteRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new NoteRepository();
        return INSTANCE;
    }

    public List<Note> search(String rawQuery) {
        String q = rawQuery == null ? "" : rawQuery.trim();
        if (q.isEmpty()) {
            return sql.query(
                    "SELECT * FROM notes ORDER BY updated_at DESC LIMIT 200",
                    NoteMapper::fromResultSet
            );
        }
        boolean tagQuery = q.startsWith(":") || q.startsWith("#");
        if (tagQuery) {
            String tag = q.replaceFirst("^[#:]+", "").toLowerCase();
            return sql.query(
                    "SELECT * FROM notes WHERE ','||tags||',' LIKE ? ORDER BY updated_at DESC",
                    NoteMapper::fromResultSet,
                    "%" + tag + "%"
            );
        }
        return sql.query(
                "SELECT n.* FROM notes n JOIN notes_fts f ON n.id = f.rowid " +
                "WHERE f.notes_fts MATCH ? ORDER BY n.updated_at DESC",
                NoteMapper::fromResultSet,
                q
        );
    }

    // Métodos create, findById, update y delete similares usando sql.exec(...) y sql.query(...)
}
