-- =============================================================================
-- MIGRACIÓN V1: Crear esquema inicial para PetMarket (Marketplace de Productos)
-- =============================================================================

-- Tabla de productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    especie_destino VARCHAR(100),
    precio DECIMAL(10,2) NOT NULL,
    atributos JSONB,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion TEXT,
    imagen_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_producto_tipo CHECK (tipo IN ('ALIMENTO', 'ACCESORIO')),
    CONSTRAINT chk_precio_positive CHECK (precio >= 0)
);

-- Tabla de inventario
CREATE TABLE inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    stock_disponible INTEGER NOT NULL DEFAULT 0,
    umbral_reposicion INTEGER NOT NULL DEFAULT 10,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT chk_stock_not_negative CHECK (stock_disponible >= 0),
    CONSTRAINT chk_umbral_positive CHECK (umbral_reposicion > 0),
    CONSTRAINT uk_inventario_producto UNIQUE (producto_id)
);

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

-- Tabla de carritos
CREATE TABLE carritos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_carrito_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    CONSTRAINT uk_carrito_cliente UNIQUE (cliente_id)
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

-- Tabla de solicitudes de adopción (externa)
CREATE TABLE solicitudes_adopcion (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    mascota_id_externa VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'RECIBIDA',
    comentarios TEXT,
    fecha_respuesta TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT chk_solicitud_estado CHECK (estado IN ('RECIBIDA', 'EN_REVISION', 'APROBADA', 'RECHAZADA'))
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_productos_tipo ON productos(tipo);
CREATE INDEX idx_productos_activo ON productos(activo);
CREATE INDEX idx_productos_especie ON productos(especie_destino);
CREATE INDEX idx_productos_precio ON productos(precio);

CREATE INDEX idx_inventario_producto_id ON inventario(producto_id);
CREATE INDEX idx_inventario_stock ON inventario(stock_disponible);

CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_activo ON clientes(activo);

CREATE INDEX idx_carritos_cliente_id ON carritos(cliente_id);
CREATE INDEX idx_carrito_items_carrito_id ON carrito_items(carrito_id);
CREATE INDEX idx_carrito_items_producto_id ON carrito_items(producto_id);

CREATE INDEX idx_pedidos_cliente_id ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_created_at ON pedidos(created_at);
CREATE INDEX idx_pedido_items_pedido_id ON pedido_items(pedido_id);

CREATE INDEX idx_solicitudes_cliente_id ON solicitudes_adopcion(cliente_id);
CREATE INDEX idx_solicitudes_estado ON solicitudes_adopcion(estado);

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

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
