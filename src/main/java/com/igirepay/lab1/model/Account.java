package com.igirepay.lab1.model;

import java.time.LocalDateTime;

public class Account {

    private int id;
    private int customerId;
    private String accountType;
    private double balance;
    private LocalDateTime createdAt;

    public Account() {}

    public Account(int id, int customerId, String accountType, double balance, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
    }
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > this.balance) throw new IllegalArgumentException("Insufficient balance.");
        this.balance -= amount;
    }

    public String processTransaction(double amount, String type) {
        return "Processing " + type + " of " + amount + " on account " + id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Account{id=" + id + ", customerId=" + customerId +
               ", type=" + accountType + ", balance=" + balance + "}";
    }
}
