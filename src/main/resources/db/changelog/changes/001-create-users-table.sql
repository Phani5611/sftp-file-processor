--liquibase formatted sql
--changeset Phani5611:create-user-table

CREATE TABLE lambda_schema.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(50),
    phone VARCHAR(20),
    gender VARCHAR(10)
);

-- rollback DROP TABLE lambda_schema.users;