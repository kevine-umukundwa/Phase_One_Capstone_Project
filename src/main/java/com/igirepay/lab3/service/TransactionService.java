package com.igirepay.lab3.service;

import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab2.dao.ProcessedRequestDAO;
import com.igirepay.lab2.dao.TransactionDAO;
import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;

import java.util.List;
import java.util.UUID;

public class TransactionService {

    private final AccountDAO          accountDAO = new AccountDAO();
    private final CustomerDAO         customerDAO = new CustomerDAO();
    private final TransactionDAO      transactionDAO = new TransactionDAO();
    private final ProcessedRequestDAO prDAO = new ProcessedRequestDAO();

    private static final double WITHDRAW_FEE = 500.0;

    public String generateRefId() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public boolean deposit(int accountId, double amount, String refId) {
        if (isDuplicate(refId)) return false;
        Account account = accountDAO.findById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found.");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");

        account.deposit(amount);
        accountDAO.updateBalance(accountId, account.getBalance());
        saveTransaction(accountId, refId, "DEPOSIT", amount);
        prDAO.markProcessed(refId);
        return true;
    }

    public boolean withdraw(int accountId, double amount, String refId) {
        if (isDuplicate(refId)) return false;
        Account account = accountDAO.findById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found.");

        double total = amount + WITHDRAW_FEE;
        if (account.getBalance() < total)
            throw new IllegalArgumentException(
                "Insufficient balance. Amount + RWF 500 fee = RWF " + total);

        account.withdraw(total);
        accountDAO.updateBalance(accountId, account.getBalance());
        saveTransaction(accountId, refId, "WITHDRAW", amount);
        prDAO.markProcessed(refId);
        return true;
    }

    public boolean transferByPhone(int fromId, String toPhone, double amount, String refId) {
        if (isDuplicate(refId)) return false;

        Account from = accountDAO.findById(fromId);
        if (from == null) throw new IllegalArgumentException("Your account not found.");

        Customer recipient = customerDAO.findByPhone(toPhone);
        if (recipient == null) throw new IllegalArgumentException("No customer found with phone: " + toPhone);


        List<Account> recipientAccounts = accountDAO.findByCustomerId(recipient.getId());
        if (recipientAccounts.isEmpty())
            throw new IllegalArgumentException("Recipient has no account to receive funds.");

        Account to = recipientAccounts.get(0);

        from.withdraw(amount);
        to.deposit(amount);

        accountDAO.updateBalance(fromId, from.getBalance());
        accountDAO.updateBalance(to.getId(), to.getBalance());

        saveTransaction(fromId, refId, "TRANSFER_OUT", amount);
        saveTransaction(to.getId(), "IN-" + refId, "TRANSFER_IN", amount);
        prDAO.markProcessed(refId);
        return true;
    }

    public List<Transaction> getHistory(int accountId) {
        return transactionDAO.findByAccountId(accountId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }

    private boolean isDuplicate(String refId) {
        if (prDAO.exists(refId)) {
            System.out.println("Duplicate rejected: " + refId);
            return true;
        }
        return false;
    }

    private void saveTransaction(int accountId, String refId, String type, double amount) {
        transactionDAO.create(new Transaction(0, accountId, refId, type, amount, null));
    }
}
