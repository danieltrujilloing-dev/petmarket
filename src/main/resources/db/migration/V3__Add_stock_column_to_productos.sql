-- =============================================================================
-- MIGRACIÓN V3: Agregar columna stock a tabla productos
-- =============================================================================

-- Agregar columna stock a la tabla productos
ALTER TABLE productos ADD COLUMN stock INTEGER;

-- Agregar constraint para que stock no sea negativo
ALTER TABLE productos ADD CONSTRAINT chk_stock_not_negative CHECK (stock >= 0);

-- Actualizar productos existentes con stock inicial basado en inventario
UPDATE productos 
SET stock = COALESCE((
    SELECT i.stock_disponible 
    FROM inventario i 
    WHERE i.producto_id = productos.id
), 0);

-- Comentario para documentar la columna
COMMENT ON COLUMN productos.stock IS 'Cantidad disponible en stock del producto';
