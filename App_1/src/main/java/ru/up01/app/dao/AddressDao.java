
package ru.up01.app.dao;

import ru.up01.app.Db;
import ru.up01.app.model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDao {

    public List<Address> findAll() throws SQLException {
        List<Address> out = new ArrayList<>();
        String sql = "SELECT id, address_line, city, street, house FROM address ORDER BY address_line";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Address(
                        rs.getInt("id"),
                        rs.getString("address_line"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("house")
                ));
            }
            return out;
        } catch (SQLException e) {
            // fallback: only address_line exists
            String sql2 = "SELECT id, address_line FROM address ORDER BY address_line";
            out.clear();
            try (Connection c = Db.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Address(rs.getInt("id"), rs.getString("address_line"), null, null, null));
                }
            }
            return out;
        }
    }
}
