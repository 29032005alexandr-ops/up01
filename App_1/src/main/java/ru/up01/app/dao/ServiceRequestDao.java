
package ru.up01.app.dao;

import ru.up01.app.Db;
import ru.up01.app.model.ServiceRequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDao {

    public List<ServiceRequest> findAllJoined() throws SQLException {
        String sql = """
            SELECT
              sr.id,
              sr.import_id,
              sr.address_id,
              sr.employee_id,
              sr.status_id,
              sr.applicant_full_name,
              sr.applicant_phone,
              sr.problem_description,
              sr.created_at,
              sr.updated_at,
              a.address_line AS address_line,
              e.full_name AS employee_name,
              rs.name AS status_name
            FROM service_request sr
            JOIN address a ON a.id = sr.address_id
            LEFT JOIN employee e ON e.id = sr.employee_id
            JOIN request_status rs ON rs.id = sr.status_id
            ORDER BY sr.id DESC
            """;

        List<ServiceRequest> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceRequest r = new ServiceRequest(
                        rs.getInt("id"),
                        rs.getString("import_id"),
                        rs.getInt("address_id"),
                        (Integer) rs.getObject("employee_id"),
                        rs.getInt("status_id"),
                        rs.getString("applicant_full_name"),
                        rs.getString("applicant_phone"),
                        rs.getString("problem_description"),
                        toLdt(rs.getTimestamp("created_at")),
                        toLdt(rs.getTimestamp("updated_at"))
                );
                r.setAddressLine(rs.getString("address_line"));
                r.setEmployeeName(rs.getString("employee_name"));
                r.setStatusName(rs.getString("status_name"));
                out.add(r);
            }
        } catch (SQLException e) {
            // If employee table uses different column names, fallback concat.
            String sql2 = """
                SELECT
                  sr.id, sr.import_id, sr.address_id, sr.employee_id, sr.status_id,
                  sr.applicant_full_name, sr.applicant_phone, sr.problem_description, sr.created_at, sr.updated_at,
                  a.address_line AS address_line,
                  CONCAT_WS(' ', e.last_name, e.first_name, e.patronymic) AS employee_name,
                  rs.name AS status_name
                FROM service_request sr
                JOIN address a ON a.id = sr.address_id
                LEFT JOIN employee e ON e.id = sr.employee_id
                JOIN request_status rs ON rs.id = sr.status_id
                ORDER BY sr.id DESC
                """;
            out.clear();
            try (Connection c = Db.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceRequest r = new ServiceRequest(
                            rs.getInt("id"),
                            rs.getString("import_id"),
                            rs.getInt("address_id"),
                            (Integer) rs.getObject("employee_id"),
                            rs.getInt("status_id"),
                            rs.getString("applicant_full_name"),
                            rs.getString("applicant_phone"),
                            rs.getString("problem_description"),
                            toLdt(rs.getTimestamp("created_at")),
                            toLdt(rs.getTimestamp("updated_at"))
                    );
                    r.setAddressLine(rs.getString("address_line"));
                    r.setEmployeeName(rs.getString("employee_name"));
                    r.setStatusName(rs.getString("status_name"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    public int insert(ServiceRequest r, String historyComment) throws SQLException {
        String sql = """
            INSERT INTO service_request
              (import_id, address_id, employee_id, status_id, applicant_full_name, applicant_phone, problem_description, created_at, updated_at)
            VALUES
              (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            """;
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, r.getImportId());
                ps.setInt(2, r.getAddressId());
                if (r.getEmployeeIdNullable() == null) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, r.getEmployeeId());
                ps.setInt(4, r.getStatusId());
                ps.setString(5, r.getApplicantFullName());
                ps.setString(6, r.getApplicantPhone());
                ps.setString(7, r.getProblemDescription());
                ps.executeUpdate();

                int newId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key returned");
                    newId = keys.getInt(1);
                }
                insertHistory(c, newId, r.getEmployeeIdNullable(), r.getStatusId(), historyComment);
                c.commit();
                return newId;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void update(ServiceRequest r, String historyComment) throws SQLException {
        String sql = """
            UPDATE service_request
            SET address_id=?, employee_id=?, status_id=?, applicant_full_name=?, applicant_phone=?, problem_description=?, updated_at=NOW()
            WHERE id=?
            """;
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, r.getAddressId());
                if (r.getEmployeeIdNullable() == null) ps.setNull(2, Types.INTEGER);
                else ps.setInt(2, r.getEmployeeId());
                ps.setInt(3, r.getStatusId());
                ps.setString(4, r.getApplicantFullName());
                ps.setString(5, r.getApplicantPhone());
                ps.setString(6, r.getProblemDescription());
                ps.setInt(7, r.getId());
                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Request not found (id=" + r.getId() + ")");
                insertHistory(c, r.getId(), r.getEmployeeIdNullable(), r.getStatusId(), historyComment);
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void deleteById(int id) throws SQLException {
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM request_history WHERE request_id=?");
                 PreparedStatement ps2 = c.prepareStatement("DELETE FROM service_request WHERE id=?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();

                ps2.setInt(1, id);
                int affected = ps2.executeUpdate();
                if (affected == 0) throw new SQLException("Request not found (id=" + id + ")");
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private static void insertHistory(Connection c, int requestId, Integer employeeId, int statusId, String comment) throws SQLException {
        String sql = "INSERT INTO request_history (request_id, employee_id, status_id, changed_at, comment) VALUES (?, ?, ?, NOW(), ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            if (employeeId == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, employeeId);
            ps.setInt(3, statusId);
            ps.setString(4, comment);
            ps.executeUpdate();
        }
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
