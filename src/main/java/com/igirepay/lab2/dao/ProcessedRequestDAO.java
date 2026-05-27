package com.igirepay.lab2.dao;

import com.igirepay.lab2.db.DBConnection;

import java.sql.*;

public class ProcessedRequestDAO {

    public boolean exists(String referenceId) {
        String sql = "SELECT id FROM processed_requests WHERE reference_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referenceId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking reference ID: " + e.getMessage());
            return false;
        }
    }

    public boolean markProcessed(String referenceId) {
        String sql = "INSERT INTO processed_requests (reference_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referenceId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error marking reference ID: " + e.getMessage());
            return false;
        }
    }
}
