-- User table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ApiRegistration table
CREATE TABLE IF NOT EXISTS api_registrations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    base_url VARCHAR(255) NOT NULL,
    swagger_url VARCHAR(255)
);

-- TotpSecret table
CREATE TABLE IF NOT EXISTS totp_secrets (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    secret_key TEXT NOT NULL,
    iv VARCHAR(24) NOT NULL  -- Initialization Vector for AES encryption
);
