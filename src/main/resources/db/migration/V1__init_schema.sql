-- V1__init_schema.sql
CREATE TABLE topics (
    id          VARCHAR(100) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

