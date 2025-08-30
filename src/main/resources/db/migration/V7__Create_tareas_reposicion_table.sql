-- Migración V7: Crear tabla de tareas de reposición
-- Autor: Sistema de Inventario Automatizado
-- Fecha: 2025-08-29
-- Descripción: Crea la tabla para gestionar tareas automáticas de reposición de stock

-- Crear tabla de tareas de reposición
CREATE TABLE tareas_reposicion (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    stock_actual INTEGER NOT NULL CHECK (stock_actual >= 0),
    umbral_reposicion INTEGER NOT NULL CHECK (umbral_reposicion >= 0),
    cantidad_sugerida INTEGER NOT NULL CHECK (cantidad_sugerida > 0),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA')),
    prioridad VARCHAR(20) NOT NULL CHECK (prioridad IN ('CRITICA', 'ALTA', 'MEDIA', 'BAJA')),
    observaciones TEXT,
    fecha_vencimiento TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_tarea_producto_id ON tareas_reposicion(producto_id);
CREATE INDEX idx_tarea_estado ON tareas_reposicion(estado);
CREATE INDEX idx_tarea_prioridad ON tareas_reposicion(prioridad);
CREATE INDEX idx_tarea_fecha_vencimiento ON tareas_reposicion(fecha_vencimiento);
CREATE INDEX idx_tarea_producto_estado ON tareas_reposicion(producto_id, estado);

-- Crear índice compuesto para consultas de tareas activas
CREATE INDEX idx_tarea_activas ON tareas_reposicion(producto_id, estado) 
WHERE estado IN ('PENDIENTE', 'EN_PROCESO');

-- Crear índice para tareas vencidas
CREATE INDEX idx_tarea_vencidas ON tareas_reposicion(fecha_vencimiento, estado) 
WHERE estado != 'COMPLETADA';

-- Comentarios en la tabla y columnas
COMMENT ON TABLE tareas_reposicion IS 'Tareas automáticas de reposición de stock generadas por eventos LowStock';
COMMENT ON COLUMN tareas_reposicion.producto_id IS 'ID del producto que necesita reposición';
COMMENT ON COLUMN tareas_reposicion.nombre_producto IS 'Nombre del producto para facilitar identificación';
COMMENT ON COLUMN tareas_reposicion.stock_actual IS 'Stock actual cuando se creó la tarea';
COMMENT ON COLUMN tareas_reposicion.umbral_reposicion IS 'Umbral de reposición del producto';
COMMENT ON COLUMN tareas_reposicion.cantidad_sugerida IS 'Cantidad sugerida para reponer';
COMMENT ON COLUMN tareas_reposicion.estado IS 'Estado actual de la tarea';
COMMENT ON COLUMN tareas_reposicion.prioridad IS 'Prioridad de la tarea basada en criticidad del stock';
COMMENT ON COLUMN tareas_reposicion.observaciones IS 'Observaciones adicionales sobre la tarea';
COMMENT ON COLUMN tareas_reposicion.fecha_vencimiento IS 'Fecha límite para completar la tarea';

-- Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_tareas_reposicion_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Crear trigger para actualizar updated_at
CREATE TRIGGER trigger_update_tareas_reposicion_updated_at
    BEFORE UPDATE ON tareas_reposicion
    FOR EACH ROW
    EXECUTE FUNCTION update_tareas_reposicion_updated_at();
