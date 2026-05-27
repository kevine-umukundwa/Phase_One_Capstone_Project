-- IgirePay Database Schema
-- Run this in your PostgreSQL database: SheCanCode

CREATE TABLE IF NOT EXISTS customers (
    id           SERIAL PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20)  UNIQUE NOT NULL,
    pin          VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id           SERIAL PRIMARY KEY,
    customer_id  INT REFERENCES customers(id) ON DELETE CASCADE,
    account_type VARCHAR(20) NOT NULL,
    balance      NUMERIC(15,2) DEFAULT 0.00,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id               SERIAL PRIMARY KEY,
    account_id       INT REFERENCES accounts(id),
    reference_id     VARCHAR(100) UNIQUE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount           NUMERIC(15,2) NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_requests (
    id           SERIAL PRIMARY KEY,
    reference_id VARCHAR(100) UNIQUE NOT NULL,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
