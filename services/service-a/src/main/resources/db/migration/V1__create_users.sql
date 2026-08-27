-- V1: create users table
CREATE TABLE IF NOT EXISTS app_user (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);