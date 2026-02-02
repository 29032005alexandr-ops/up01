
package ru.up01.app.dao;

import ru.up01.app.Db;
import ru.up01.app.model.HistoryRow;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryDao {

    public List<HistoryRow> findHistory(Integer addressId, Integer employeeId) throws SQLException {
        String sql = """
            SELECT
              rh.id AS history_id,
              sr.id AS request_id,
              a.address_line,
              COALESCE(e.full_name, '') AS employee_name,
              rs.name AS status_name,
              rh.changed_at,
              rh.comment
            FROM request_history rh
            JOIN service_request sr ON sr.id = rh.request_id
            JOIN address a ON a.id = sr.address_id
            LEFT JOIN employee e ON e.id = rh.employee_id
            JOIN request_status rs ON rs.id = rh.status_id
            WHERE (? IS NULL OR sr.address_id = ?)
              AND (? IS NULL OR rh.employee_id = ?)
            ORDER BY rh.changed_at DESC, rh.id DESC
            """;

        List<HistoryRow> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            setNullableInt(ps, 1, addressId);
            setNullableInt(ps, 2, addressId);
            setNullableInt(ps, 3, employeeId);
            setNullableInt(ps, 4, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new HistoryRow(
                            rs.getInt("history_id"),
                            rs.getInt("request_id"),
                            rs.getString("address_line"),
                            rs.getString("employee_name"),
                            rs.getString("status_name"),
                            toLdt(rs.getTimestamp("changed_at")),
                            rs.getString("comment")
                    ));
                }
            }
        } catch (SQLException e) {
            // fallback for employee name parts
            String sql2 = """
                SELECT
                  rh.id AS history_id,
                  sr.id AS request_id,
                  a.address_line,
                  CONCAT_WS(' ', e.last_name, e.first_name, e.patronymic) AS employee_name,
                  rs.name AS status_name,
                  rh.changed_at,
                  rh.comment
                FROM request_history rh
                JOIN service_request sr ON sr.id = rh.request_id
                JOIN address a ON a.id = sr.address_id
                LEFT JOIN employee e ON e.id = rh.employee_id
                JOIN request_status rs ON rs.id = rh.status_id
                WHERE (? IS NULL OR sr.address_id = ?)
                  AND (? IS NULL OR rh.employee_id = ?)
                ORDER BY rh.changed_at DESC, rh.id DESC
                """;
            out.clear();
            try (Connection c = Db.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql2)) {
                setNullableInt(ps, 1, addressId);
                setNullableInt(ps, 2, addressId);
                setNullableInt(ps, 3, employeeId);
                setNullableInt(ps, 4, employeeId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new HistoryRow(
                                rs.getInt("history_id"),
                                rs.getInt("request_id"),
                                rs.getString("address_line"),
                                rs.getString("employee_name"),
                                rs.getString("status_name"),
                                toLdt(rs.getTimestamp("changed_at")),
                                rs.getString("comment")
                        ));
                    }
                }
            }
        }

        return out;
    }

    private static void setNullableInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v);
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
