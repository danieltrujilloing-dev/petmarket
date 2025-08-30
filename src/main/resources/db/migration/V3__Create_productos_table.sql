-- Migración para actualizar la tabla de productos
-- Versión: 3
-- Descripción: Add stock column and additional indexes

-- Agregar columna stock si no existe
ALTER TABLE productos ADD COLUMN IF NOT EXISTS stock INTEGER CHECK (stock >= 0);

-- Crear índices adicionales para mejor performance
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON productos(nombre);
CREATE INDEX IF NOT EXISTS idx_producto_tipo_especie_destino ON productos(tipo, especie_destino);

-- Comentarios en la tabla
COMMENT ON TABLE productos IS 'Catálogo de productos del marketplace PetMarket';
COMMENT ON COLUMN productos.tipo IS 'Tipo de producto: alimento, accesorio, juguete, etc.';
COMMENT ON COLUMN productos.especie_destino IS 'Especie animal para la cual está destinado el producto';
COMMENT ON COLUMN productos.stock IS 'Cantidad disponible en inventario';
COMMENT ON COLUMN productos.activo IS 'Indica si el producto está activo y visible en el catálogo';
COMMENT ON COLUMN productos.imagen_url IS 'URL de la imagen principal del producto';
