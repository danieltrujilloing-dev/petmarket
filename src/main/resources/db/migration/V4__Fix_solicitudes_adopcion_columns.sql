-- =============================================================================
-- MIGRACIÓN V4: Arreglar nombres de columnas en tabla solicitudes_adopcion
-- =============================================================================

-- Renombrar columnas para que coincidan con la entidad JPA
ALTER TABLE solicitudes_adopcion RENAME COLUMN created_at TO fecha_creacion;
ALTER TABLE solicitudes_adopcion RENAME COLUMN updated_at TO fecha_actualizacion;

-- Comentarios para documentar las columnas
COMMENT ON COLUMN solicitudes_adopcion.fecha_creacion IS 'Fecha de creación de la solicitud';
COMMENT ON COLUMN solicitudes_adopcion.fecha_actualizacion IS 'Fecha de última actualización de la solicitud';
