package com.igirepay.lab1.model;

import java.time.LocalDateTime;

public class WalletAccount extends Account {

    public WalletAccount() {
        super();
        setAccountType("WALLET");
    }

    public WalletAccount(int id, int customerId, double balance, LocalDateTime createdAt) {
        super(id, customerId, "WALLET", balance, createdAt);
    }

    @Override
    public void deposit(double amount) {
        System.out.println("Wallet: Instant deposit of " + amount);
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Wallet: Instant withdrawal of " + amount);
        super.withdraw(amount);
    }

    @Override
    public String processTransaction(double amount, String type) {
        return "WalletAccount: Instant " + type + " of RWF " + amount;
    }

    @Override
    public String toString() {
        return "WalletAccount{id=" + getId() + ", balance=" + getBalance() + "}";
    }
}
