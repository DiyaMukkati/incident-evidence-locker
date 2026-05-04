-- ================================================================
-- V3__users.sql — Tool-36 Incident Evidence Locker
-- Creates the users table for JWT authentication
-- Day 5 — Java Developer 1
-- ================================================================

CREATE TABLE users (
    id         BIGSERIAL     PRIMARY KEY,
    username   VARCHAR(50)   NOT NULL UNIQUE,
    email      VARCHAR(100)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    role       VARCHAR(20)   NOT NULL DEFAULT 'USER',
    is_active  BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email    ON users(email);

-- Insert a default admin user (password: Admin@123)
-- BCrypt hash of 'Admin@123'
INSERT INTO users (username, email, password, role)
VALUES (
    'admin',
    'admin@tool36.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Zkq2',
    'ADMIN'
);
