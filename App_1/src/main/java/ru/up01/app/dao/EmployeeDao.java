
package ru.up01.app.dao;

import ru.up01.app.Db;
import ru.up01.app.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    public List<Employee> findAll() throws SQLException {
        // Column names in your schema may differ. Try common set first.
        String sql = "SELECT id, full_name, phone, email, position FROM employee ORDER BY full_name";
        List<Employee> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("position")
                ));
            }
        } catch (SQLException e) {
            // fallback for schemas where name stored as first/last parts
            String sql2 = "SELECT id, CONCAT_WS(' ', last_name, first_name, patronymic) AS full_name, phone, email, position FROM employee ORDER BY last_name, first_name";
            out.clear();
            try (Connection c = Db.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Employee(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("position")
                    ));
                }
            }
        }
        return out;
    }
}
