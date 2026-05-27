package com.igirepay.lab3.app;

import com.igirepay.lab2.db.SchemaSetup;
import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab3.service.AccountService;
import com.igirepay.lab3.service.CustomerService;
import com.igirepay.lab3.service.TransactionService;

import java.util.List;
import java.util.Scanner;

public class IgirePayApp {

    static Scanner scanner             = new Scanner(System.in);
    static CustomerService customerSvc = new CustomerService();
    static AccountService accountSvc   = new AccountService();
    static TransactionService txSvc    = new TransactionService();

    static Customer loggedInCustomer = null;

    public static void main(String[] args) {
        SchemaSetup.createTables();

        System.out.println("   Welcome to IgirePay System   ");
        System.out.println("=====================================");

        while (true) {
            if (loggedInCustomer == null) {
                showGuestMenu();
            } else {
                showMainMenu();
            }
        }
    }


    static void showGuestMenu() {
        System.out.println("\n1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> registerCustomer();
            case "2" -> loginCustomer();
            case "0" -> { System.out.println("Goodbye!"); System.exit(0); }
            default  -> System.out.println("Invalid option.");
        }
    }

    static void showMainMenu() {
        System.out.println("\n--- Hello, " + loggedInCustomer.getFullName() + " ---");
        System.out.println(" 1. Create Account (Wallet or Savings)");
        System.out.println(" 2. View My Accounts");
        System.out.println(" 3. Deposit Money");
        System.out.println(" 4. Withdraw Money");
        System.out.println(" 5. Transfer Money");
        System.out.println(" 6. View Transaction History");
        System.out.println(" 7. Change PIN");
        System.out.println(" 0. Logout");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1"  -> createAccount();
            case "2"  -> viewAccounts();
            case "3"  -> depositMoney();
            case "4"  -> withdrawMoney();
            case "5"  -> transferMoney();
            case "6"  -> viewHistory();
            case "7"  -> changePin();
            case "0"  -> { loggedInCustomer = null; System.out.println("Logged out."); }
            default   -> System.out.println("Invalid option.");
        }
    }

    static void registerCustomer() {
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Create 5-digit PIN: ");
        String pin = scanner.nextLine();

        boolean ok = customerSvc.register(name,email, phone, pin);
        System.out.println(ok ? "Registration successful!" : "Registration failed.");
    }

    static void loginCustomer() {
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("PIN: ");
        String pin = scanner.nextLine();

        loggedInCustomer = customerSvc.login(phone, pin);
        if (loggedInCustomer != null) {
            System.out.println("Login successful! Welcome, " + loggedInCustomer.getFullName());
        }
    }

    static void createAccount() {
        System.out.println("Account Type:");
        System.out.println("  1. Wallet Account");
        System.out.println("  2. Savings Account");
        System.out.print("Choose: ");
        String type = scanner.nextLine().trim();

        boolean ok = switch (type) {
            case "1" -> accountSvc.createWallet(loggedInCustomer.getId());
            case "2" -> accountSvc.createSavings(loggedInCustomer.getId());
            default  -> { System.out.println("Invalid choice."); yield false; }
        };

        if (ok) System.out.println("Account created successfully.");
    }


    static void viewAccounts() {
        List<Account> accounts = accountSvc.getAccountsByCustomer(loggedInCustomer.getId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            accounts.forEach(a -> System.out.println(
                "  ID: " + a.getId() + " | Type: " + a.getAccountType() + " | Balance: RWF " + a.getBalance()
            ));
        }
    }


    static void depositMoney() {
        try {
            System.out.print("Account ID: ");
            int accId = Integer.parseInt(scanner.nextLine());
            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            txSvc.deposit(accId, amount, txSvc.generateRefId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void withdrawMoney() {
        try {
            System.out.print("Account ID: ");
            int accId = Integer.parseInt(scanner.nextLine());
            System.out.print("Amount (RWF 500 fee will be charged): ");
            double amount = Double.parseDouble(scanner.nextLine());
            txSvc.withdraw(accId, amount, txSvc.generateRefId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void transferMoney() {
        try {
            System.out.print("From Account ID: ");
            int fromId = Integer.parseInt(scanner.nextLine());
            System.out.print("Recipient Phone Number: ");
            String toPhone = scanner.nextLine().trim();
            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            txSvc.transferByPhone(fromId, toPhone, amount, txSvc.generateRefId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void viewHistory() {
        try {
            System.out.print("Account ID: ");
            int accId = Integer.parseInt(scanner.nextLine());
            List<Transaction> history = txSvc.getHistory(accId);
            if (history.isEmpty()) System.out.println("No transactions found.");
            else history.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void changePin() {
        System.out.print("Current PIN: ");
        String oldPin = scanner.nextLine();
        System.out.print("New PIN: ");
        String newPin = scanner.nextLine();
        boolean ok = customerSvc.changePin(loggedInCustomer.getId(), oldPin, newPin);
        System.out.println(ok ? "PIN changed successfully." : "PIN change failed.");
    }
}
