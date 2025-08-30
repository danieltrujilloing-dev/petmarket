-- Migración V6: Insertar datos de inventario para productos existentes

-- Insertar inventario para todos los productos existentes
INSERT INTO inventario (producto_id, stock_disponible, umbral_reposicion) 
SELECT 
    p.id,
    CASE 
        WHEN p.tipo = 'ALIMENTO' THEN 50 + (p.id % 50)  -- Stock entre 50-100 para alimentos
        WHEN p.tipo = 'ACCESORIO' THEN 20 + (p.id % 30) -- Stock entre 20-50 para accesorios
        ELSE 25 + (p.id % 25)                           -- Stock entre 25-50 para otros
    END as stock_disponible,
    CASE 
        WHEN p.tipo = 'ALIMENTO' THEN 15    -- Umbral más alto para alimentos
        WHEN p.tipo = 'ACCESORIO' THEN 5    -- Umbral más bajo para accesorios
        ELSE 10                             -- Umbral medio para otros
    END as umbral_reposicion
FROM productos p
WHERE p.activo = true
ON CONFLICT (producto_id) DO NOTHING;

-- Comentarios para documentar la lógica
COMMENT ON TABLE inventario IS 'Tabla de inventario que mantiene el stock disponible y umbral de reposición por producto';
COMMENT ON COLUMN inventario.stock_disponible IS 'Cantidad actual disponible en inventario';
COMMENT ON COLUMN inventario.umbral_reposicion IS 'Cantidad mínima antes de requerir reposición';
COMMENT ON COLUMN inventario.producto_id IS 'Referencia al producto (relación 1:1)';

-- Verificar que todos los productos activos tienen inventario
DO $$
DECLARE
    productos_sin_inventario INTEGER;
BEGIN
    SELECT COUNT(*) INTO productos_sin_inventario
    FROM productos p
    LEFT JOIN inventario i ON p.id = i.producto_id
    WHERE p.activo = true AND i.producto_id IS NULL;
    
    IF productos_sin_inventario > 0 THEN
        RAISE NOTICE 'Advertencia: % productos activos sin inventario', productos_sin_inventario;
    ELSE
        RAISE NOTICE 'Todos los productos activos tienen inventario configurado';
    END IF;
END $$;
