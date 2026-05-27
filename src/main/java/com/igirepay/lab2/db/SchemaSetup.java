package com.igirepay.lab2.db;

import java.sql.Connection;
import java.sql.Statement;

public class SchemaSetup {

    public static void createTables() {
        String customers = """
            CREATE TABLE IF NOT EXISTS customers (
                id           SERIAL PRIMARY KEY,
                full_name    VARCHAR(100) NOT NULL,
                email        VARCHAR(100) UNIQUE NOT NULL,
                phone_number VARCHAR(20)  UNIQUE NOT NULL,
                pin          VARCHAR(100) NOT NULL
            );
            """;

        String accounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                id           SERIAL PRIMARY KEY,
                customer_id  INT REFERENCES customers(id) ON DELETE CASCADE,
                account_type VARCHAR(20) NOT NULL,
                balance      NUMERIC(15,2) DEFAULT 0.00,
                created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id               SERIAL PRIMARY KEY,
                account_id       INT REFERENCES accounts(id),
                reference_id     VARCHAR(100) UNIQUE NOT NULL,
                transaction_type VARCHAR(20) NOT NULL,
                amount           NUMERIC(15,2) NOT NULL,
                created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        String processedRequests = """
            CREATE TABLE IF NOT EXISTS processed_requests (
                id           SERIAL PRIMARY KEY,
                reference_id VARCHAR(100) UNIQUE NOT NULL,
                processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(customers);
            stmt.execute(accounts);
            stmt.execute(transactions);
            stmt.execute(processedRequests);

        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}
