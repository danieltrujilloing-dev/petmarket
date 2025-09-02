-- =============================================================================
-- MIGRACIÓN V1: Crear esquema inicial completo para PetMarket
-- =============================================================================

-- Tabla de clientes
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
);

-- Tabla de productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    especie_destino VARCHAR(100),
    precio DECIMAL(10,2) NOT NULL,
    descripcion TEXT,
    imagen_url VARCHAR(500),
    atributos JSONB,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_producto_tipo CHECK (tipo IN ('ALIMENTO', 'ACCESORIO')),
    CONSTRAINT chk_precio_positive CHECK (precio >= 0)
);

-- Tabla de inventario
CREATE TABLE inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE,
    stock_disponible INTEGER NOT NULL DEFAULT 0,
    umbral_reposicion INTEGER NOT NULL DEFAULT 10,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT chk_stock_not_negative CHECK (stock_disponible >= 0),
    CONSTRAINT chk_umbral_positive CHECK (umbral_reposicion > 0)
);

-- Tabla de carritos
CREATE TABLE carritos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_carrito_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Tabla de items del carrito
CREATE TABLE carrito_items (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_carrito_item_carrito FOREIGN KEY (carrito_id) REFERENCES carritos(id) ON DELETE CASCADE,
    CONSTRAINT fk_carrito_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT chk_cantidad_positive CHECK (cantidad > 0),
    CONSTRAINT chk_precio_unitario_positive CHECK (precio_unitario >= 0),
    CONSTRAINT uk_carrito_producto UNIQUE (carrito_id, producto_id)
);

-- Tabla de pedidos
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'CREADO',
    direccion_envio TEXT NOT NULL,
    notas TEXT,
    fecha_estimada_entrega DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('CREADO', 'PAGADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO')),
    CONSTRAINT chk_total_positive CHECK (total >= 0)
);

-- Tabla de items del pedido
CREATE TABLE pedido_items (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_pedido_item_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT chk_item_cantidad_positive CHECK (cantidad > 0),
    CONSTRAINT chk_item_precio_positive CHECK (precio_unitario >= 0),
    CONSTRAINT chk_subtotal_positive CHECK (subtotal >= 0)
);

-- Tabla de solicitudes de adopción
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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Tabla de tareas de reposición
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_tarea_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_productos_tipo ON productos(tipo);
CREATE INDEX idx_productos_activo ON productos(activo);
CREATE INDEX idx_productos_especie ON productos(especie_destino);
CREATE INDEX idx_productos_precio ON productos(precio);
CREATE INDEX idx_productos_nombre ON productos(nombre);

CREATE INDEX idx_inventario_producto_id ON inventario(producto_id);
CREATE INDEX idx_inventario_stock ON inventario(stock_disponible);
CREATE INDEX idx_inventario_stock_bajo ON inventario(stock_disponible, umbral_reposicion);

CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_activo ON clientes(activo);

CREATE INDEX idx_carritos_cliente_id ON carritos(cliente_id);
CREATE INDEX idx_carrito_items_carrito_id ON carrito_items(carrito_id);
CREATE INDEX idx_carrito_items_producto_id ON carrito_items(producto_id);

CREATE INDEX idx_pedidos_cliente_id ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_created_at ON pedidos(created_at);
CREATE INDEX idx_pedido_items_pedido_id ON pedido_items(pedido_id);
CREATE INDEX idx_pedido_items_producto_id ON pedido_items(producto_id);

CREATE INDEX idx_solicitudes_cliente_id ON solicitudes_adopcion(cliente_id);
CREATE INDEX idx_solicitudes_estado ON solicitudes_adopcion(estado);
CREATE INDEX idx_solicitudes_tipo_mascota ON solicitudes_adopcion(tipo_mascota_deseada);
CREATE INDEX idx_solicitudes_refugio ON solicitudes_adopcion(refugio_asignado);
CREATE INDEX idx_solicitudes_fecha_creacion ON solicitudes_adopcion(created_at);
CREATE INDEX idx_solicitudes_cliente_estado ON solicitudes_adopcion(cliente_id, estado);

CREATE INDEX idx_tarea_producto_id ON tareas_reposicion(producto_id);
CREATE INDEX idx_tarea_estado ON tareas_reposicion(estado);
CREATE INDEX idx_tarea_prioridad ON tareas_reposicion(prioridad);
CREATE INDEX idx_tarea_fecha_vencimiento ON tareas_reposicion(fecha_vencimiento);
CREATE INDEX idx_tarea_producto_estado ON tareas_reposicion(producto_id, estado);
CREATE INDEX idx_tarea_activas ON tareas_reposicion(producto_id, estado) WHERE estado IN ('PENDIENTE', 'EN_PROCESO');
CREATE INDEX idx_tarea_vencidas ON tareas_reposicion(fecha_vencimiento, estado) WHERE estado != 'COMPLETADA';

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para actualizar updated_at automáticamente
CREATE TRIGGER update_productos_updated_at BEFORE UPDATE ON productos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventario_updated_at BEFORE UPDATE ON inventario
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_clientes_updated_at BEFORE UPDATE ON clientes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_carritos_updated_at BEFORE UPDATE ON carritos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_pedidos_updated_at BEFORE UPDATE ON pedidos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_solicitudes_updated_at BEFORE UPDATE ON solicitudes_adopcion
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tareas_reposicion_updated_at BEFORE UPDATE ON tareas_reposicion
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comentarios en las tablas
COMMENT ON TABLE productos IS 'Catálogo de productos del marketplace PetMarket';
COMMENT ON TABLE inventario IS 'Tabla de inventario que mantiene el stock disponible y umbral de reposición por producto';
COMMENT ON TABLE solicitudes_adopcion IS 'Tabla para almacenar solicitudes de adopción de mascotas';
COMMENT ON TABLE tareas_reposicion IS 'Tareas automáticas de reposición de stock generadas por eventos LowStock';