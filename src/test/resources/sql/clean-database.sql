-- Script para limpiar la base de datos antes de cada test
-- Se ejecuta en orden inverso a las dependencias para evitar errores de foreign key

-- Limpiar tablas en orden correcto
DELETE FROM carrito_items;
DELETE FROM carritos;
DELETE FROM pedido_items;
DELETE FROM pedidos;
DELETE FROM solicitudes_adopcion;
DELETE FROM inventario;
DELETE FROM productos;
DELETE FROM clientes;

-- Reiniciar secuencias
ALTER SEQUENCE IF EXISTS productos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS clientes_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS carritos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS pedidos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS solicitudes_adopcion_id_seq RESTART WITH 1;
