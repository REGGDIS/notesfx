// Convierte ResultSet en Note
package com.regdevs.notesfx.storage;

import com.regdevs.notesfx.model.Note;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NoteMapper {
    public static Note fromResultSet(ResultSet rs) {
        try {
            return new Note(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("tags"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapeando Note desde ResultSet", e);
        }
    }
}
