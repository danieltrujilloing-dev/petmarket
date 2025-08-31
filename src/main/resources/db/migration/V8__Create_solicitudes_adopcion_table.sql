-- =============================================================================
-- Migración V8: Crear tabla de solicitudes de adopción
-- =============================================================================

-- Crear tabla solicitudes_adopcion
CREATE TABLE solicitudes_adopcion (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    nombre_solicitante VARCHAR(100) NOT NULL,
    email_solicitante VARCHAR(100) NOT NULL,
    telefono_solicitante VARCHAR(20),
    tipo_mascota_deseada VARCHAR(20) NOT NULL CHECK (tipo_mascota_deseada IN ('PERRO', 'GATO', 'AVE', 'CONEJO', 'HAMSTER', 'OTRO')),
    motivo_adopcion TEXT NOT NULL,
    experiencia_previa TEXT,
    situacion_vivienda TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'EN_VERIFICACION', 'APROBADA', 'RECHAZADA', 'COMPLETADA', 'CANCELADA')),
    observaciones_refugio TEXT,
    motivo_rechazo TEXT,
    fecha_verificacion TIMESTAMP,
    refugio_asignado VARCHAR(100),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_solicitudes_adopcion_cliente_id ON solicitudes_adopcion(cliente_id);
CREATE INDEX idx_solicitudes_adopcion_estado ON solicitudes_adopcion(estado);
CREATE INDEX idx_solicitudes_adopcion_tipo_mascota ON solicitudes_adopcion(tipo_mascota_deseada);
CREATE INDEX idx_solicitudes_adopcion_refugio ON solicitudes_adopcion(refugio_asignado);
CREATE INDEX idx_solicitudes_adopcion_fecha_creacion ON solicitudes_adopcion(fecha_creacion);

-- Crear índice compuesto para consultas frecuentes
CREATE INDEX idx_solicitudes_adopcion_cliente_estado ON solicitudes_adopcion(cliente_id, estado);

-- Comentarios en la tabla
COMMENT ON TABLE solicitudes_adopcion IS 'Tabla para almacenar solicitudes de adopción de mascotas';
COMMENT ON COLUMN solicitudes_adopcion.cliente_id IS 'ID del cliente que solicita la adopción';
COMMENT ON COLUMN solicitudes_adopcion.nombre_solicitante IS 'Nombre completo del solicitante';
COMMENT ON COLUMN solicitudes_adopcion.email_solicitante IS 'Email de contacto del solicitante';
COMMENT ON COLUMN solicitudes_adopcion.telefono_solicitante IS 'Teléfono de contacto del solicitante';
COMMENT ON COLUMN solicitudes_adopcion.tipo_mascota_deseada IS 'Tipo de mascota que desea adoptar';
COMMENT ON COLUMN solicitudes_adopcion.motivo_adopcion IS 'Razón por la cual desea adoptar una mascota';
COMMENT ON COLUMN solicitudes_adopcion.experiencia_previa IS 'Experiencia previa con mascotas';
COMMENT ON COLUMN solicitudes_adopcion.situacion_vivienda IS 'Descripción de la situación de vivienda';
COMMENT ON COLUMN solicitudes_adopcion.estado IS 'Estado actual de la solicitud';
COMMENT ON COLUMN solicitudes_adopcion.observaciones_refugio IS 'Observaciones del refugio sobre la solicitud';
COMMENT ON COLUMN solicitudes_adopcion.motivo_rechazo IS 'Motivo de rechazo si la solicitud fue rechazada';
COMMENT ON COLUMN solicitudes_adopcion.fecha_verificacion IS 'Fecha en que se inició la verificación';
COMMENT ON COLUMN solicitudes_adopcion.refugio_asignado IS 'Refugio asignado para la verificación';
COMMENT ON COLUMN solicitudes_adopcion.fecha_creacion IS 'Fecha de creación del registro';
COMMENT ON COLUMN solicitudes_adopcion.fecha_actualizacion IS 'Fecha de última actualización del registro';
