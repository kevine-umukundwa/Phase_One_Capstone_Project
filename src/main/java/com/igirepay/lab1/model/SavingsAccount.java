package com.igirepay.lab1.model;

import java.time.LocalDateTime;

public class SavingsAccount extends Account {

    private static final double WITHDRAWAL_FEE = 0.02;
    private static final double MIN_BALANCE    = 500.0;

    public SavingsAccount() {
        super();
        setAccountType("SAVINGS");
    }

    public SavingsAccount(int id, int customerId, double balance, LocalDateTime createdAt) {
        super(id, customerId, "SAVINGS", balance, createdAt);
    }

    @Override
    public void deposit(double amount) {
        System.out.println("Savings: Depositing " + amount);
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) {
        double fee   = amount * WITHDRAWAL_FEE;
        double total = amount + fee;

        if (getBalance() - total < MIN_BALANCE) {
            throw new IllegalArgumentException(
                "Cannot withdraw. Minimum balance of " + MIN_BALANCE +
                " must be maintained. Fee applied: " + fee
            );
        }
        System.out.println("Savings: Withdrawing " + amount + " with fee " + fee);
        super.withdraw(total);
    }

    @Override
    public String processTransaction(double amount, String type) {
        return "SavingsAccount: " + type + " of RWF " + amount + " (2% fee applies on withdrawal)";
    }

    @Override
    public String toString() {
        return "SavingsAccount{id=" + getId() + ", balance=" + getBalance() + "}";
    }
}
