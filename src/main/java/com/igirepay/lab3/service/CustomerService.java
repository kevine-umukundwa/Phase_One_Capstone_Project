package com.igirepay.lab3.service;

import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab1.model.Customer;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public boolean register(String fullName, String email, String phone, String pin) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || pin.isBlank()) {
            System.out.println("All fields are required.");
            return false;
        }
        if (!pin.matches("\\d{5}")) {
            System.out.println("PIN must be exactly 5 numeric digits.");
            return false;
        }
        Customer c = new Customer(0, fullName, email, phone, pin);
        return customerDAO.create(c);
    }

    public Customer login(String phone, String pin) {
        Customer c = customerDAO.findByPhone(phone);
        if (c == null) {
            System.out.println("Customer not found.");
            return null;
        }
        if (!c.getPin().equals(pin)) {
            System.out.println("Incorrect PIN.");
            return null;
        }
        return c;
    }

    public boolean updateInfo(int id, String fullName, String email, String phone) {
        Customer c = customerDAO.findById(id);
        if (c == null) return false;
        c.setFullName(fullName);
        c.setEmail(email);
        c.setPhoneNumber(phone);
        return customerDAO.update(c);
    }

    public boolean changePin(int customerId, String oldPin, String newPin) {
        Customer c = customerDAO.findById(customerId);
        if (c == null || !c.getPin().equals(oldPin)) {
            System.out.println("Old PIN is incorrect.");
            return false;
        }
        if (!newPin.matches("\\d{5}")) {
            System.out.println("New PIN must be exactly 5 numeric digits.");
            return false;
        }
        return customerDAO.updatePin(customerId, newPin);
    }

    public Customer findById(int id) {
        return customerDAO.findById(id);
    }

    public List<Customer> findAll() {
        return customerDAO.findAll();
    }
}
