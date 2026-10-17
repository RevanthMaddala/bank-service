-- Creates a local development database and an application user for bank-service.
-- Run as a PostgreSQL superuser:
--   psql -h localhost -U postgres -f scripts/create-local-db.sql
-- The password below is for LOCAL DEVELOPMENT ONLY; use a secret store in real environments.

CREATE ROLE bank WITH LOGIN PASSWORD 'bank';
CREATE DATABASE bankdb OWNER bank;
