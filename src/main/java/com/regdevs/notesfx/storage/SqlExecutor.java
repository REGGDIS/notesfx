// Helper genérico para exec/update/query
package com.regdevs.notesfx.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SqlExecutor {
    private final Connection conn;

    public SqlExecutor(Connection conn) {
        this.conn = conn;
    }

    public void exec(String sql, Object... params) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(String sql, Function<ResultSet, T> mapper, Object... params) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.apply(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
