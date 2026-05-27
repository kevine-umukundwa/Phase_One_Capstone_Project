package com.igirepay.lab1.collections;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionManager {

    private List<Customer> customers = new ArrayList<>();

    private Map<Integer, Account> accounts = new HashMap<>();

    private List<Transaction> transactionHistory = new ArrayList<>();

    private Set<String> processedRefIds = new HashSet<>();

    private List<String> failedTransactionLogs = new ArrayList<>();


    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added: " + customer.getFullName());
    }

    public List<Customer> getCustomers() {
        return customers;
    }


    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
        System.out.println("Account added: " + account);
    }

    public Account getAccount(int accountId) {
        return accounts.get(accountId);
    }


    public boolean processTransaction(Transaction transaction) {
        String refId = transaction.getReferenceId();

        if (processedRefIds.contains(refId)) {
            String log = "DUPLICATE REJECTED: " + refId + " at " + LocalDateTime.now();
            failedTransactionLogs.add(log);
            System.out.println(log);
            return false;
        }


        Account account = accounts.get(transaction.getAccountId());
        if (account == null) {
            failedTransactionLogs.add("FAILED: Account not found for ref " + refId);
            return false;
        }

        try {
            if ("DEPOSIT".equals(transaction.getTransactionType())) {
                account.deposit(transaction.getAmount());
            } else if ("WITHDRAW".equals(transaction.getTransactionType())) {
                account.withdraw(transaction.getAmount());
            }

            processedRefIds.add(refId);
            transactionHistory.add(transaction);
            System.out.println("Transaction processed: " + transaction);
            return true;

        } catch (Exception e) {
            failedTransactionLogs.add("FAILED: " + e.getMessage() + " | ref=" + refId);
            System.out.println("Transaction failed: " + e.getMessage());
            return false;
        }
    }

    public void printHistory() {
        System.out.println("\n--- Transaction History ---");
        transactionHistory.forEach(System.out::println);
    }

    public void printFailedLogs() {
        System.out.println("\n--- Failed Transaction Logs ---");
        failedTransactionLogs.forEach(System.out::println);
    }

    public Set<String> getProcessedRefIds() {
        return processedRefIds;
    }
}
