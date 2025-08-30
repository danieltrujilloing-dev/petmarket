-- Script de inicialización para PostgreSQL
-- Se ejecuta automáticamente cuando se crea el contenedor

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Crear esquemas adicionales si es necesario
-- CREATE SCHEMA IF NOT EXISTS petmarket_schema;

-- Tabla de ejemplo (puedes eliminarla después)
CREATE TABLE IF NOT EXISTS health_check (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de prueba
INSERT INTO health_check (status) VALUES ('Database initialized successfully');

-- Comentarios para futuras tablas
-- Aquí puedes agregar tus tablas cuando las definas:
-- CREATE TABLE pets (...);
-- CREATE TABLE orders (...);
-- etc.
