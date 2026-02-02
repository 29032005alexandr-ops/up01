
package ru.up01.app.dao;

import ru.up01.app.Db;
import ru.up01.app.model.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusDao {

    public List<RequestStatus> findAll() throws SQLException {
        String sql = "SELECT id, name FROM request_status ORDER BY id";
        List<RequestStatus> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new RequestStatus(rs.getInt("id"), rs.getString("name")));
            }
        }
        return out;
    }
}
