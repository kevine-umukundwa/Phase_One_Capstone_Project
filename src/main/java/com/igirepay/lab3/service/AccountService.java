package com.igirepay.lab3.service;

import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.SavingsAccount;
import com.igirepay.lab1.model.WalletAccount;

import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();

    public boolean createWallet(int customerId) {
        WalletAccount wallet = new WalletAccount();
        wallet.setCustomerId(customerId);
        wallet.setBalance(0);
        boolean ok = accountDAO.create(wallet);
        if (ok) System.out.println("Wallet account created successfully.");
        return ok;
    }

    public boolean createSavings(int customerId) {
        SavingsAccount savings = new SavingsAccount();
        savings.setCustomerId(customerId);
        savings.setBalance(0);
        boolean ok = accountDAO.create(savings);
        if (ok) System.out.println("Savings account created successfully.");
        return ok;
    }

    public Account findById(int id) {
        return accountDAO.findById(id);
    }

    public List<Account> getAccountsByCustomer(int customerId) {
        return accountDAO.findByCustomerId(customerId);
    }


    public boolean updateBalance(int accountId, double newBalance) {
        return accountDAO.updateBalance(accountId, newBalance);
    }

    public boolean deleteAccount(int accountId) {
        return accountDAO.delete(accountId);
    }
}
