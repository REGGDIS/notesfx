// Lógica alta de CRUD/FTS
package com.regdevs.notesfx.storage;

import com.regdevs.notesfx.model.Note;

import java.sql.Connection;
import java.sql.SQLException;
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

    public Note create(String title, String content, Set<String> tagSet) {
        String now = Note.isoNow();
        sql.exec(
                "INSERT INTO notes(title, content, tags, created_at, updated_at) VALUES (?,?,?,?,?)",
                title,
                content,
                Note.normalizeTags(tagSet),
                now,
                now
        );
        long id = sql.query(
                "SELECT last_insert_rowid()",
                rs -> {
                    try {
                        return rs.getLong(1);
                    } catch (SQLException e) {
                        throw new RuntimeException("No se pudo obtener last_insert_rowid", e);
                    }
                }
        ).get(0);
        return findById(id);
    }

    public Note findById(long id) {
        return sql.query(
                "SELECT * FROM notes WHERE id = ?",
                NoteMapper::fromResultSet,
                id
        ).stream()
                .findFirst()
                .orElse(null);
    }

    public void update(long id, String title, String content, Set<String> tagSet) {
        String now = Note.isoNow();
        sql.exec(
                "UPDATE notes SET title = ?, content = ?, tags = ?, updated_at = ? WHERE id = ?",
                title,
                content,
                Note.normalizeTags(tagSet),
                now,
                id
        );
    }

    public void delete(long id) {
        sql.exec(
                "DELETE FROM notes WHERE id = ?",
                id
        );
    }
}
