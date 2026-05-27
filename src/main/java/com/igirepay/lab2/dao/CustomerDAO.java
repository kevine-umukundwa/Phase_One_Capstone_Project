package com.igirepay.lab2.dao;

import com.igirepay.lab2.db.DBConnection;
import com.igirepay.lab1.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public boolean create(Customer c) {
        String sql = "INSERT INTO customers (full_name, email, phone_number, pin) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhoneNumber());
            ps.setString(4, c.getPin());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

   
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }
        return null;
    }

    public Customer findByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.out.println("Error listing customers: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Customer c) {
        String sql = "UPDATE customers SET full_name=?, email=?, phone_number=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhoneNumber());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

 
    public boolean updatePin(int customerId, String newPin) {
        String sql = "UPDATE customers SET pin=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPin);
            ps.setInt(2, customerId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating PIN: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone_number"),
            rs.getString("pin")
        );
    }
}
