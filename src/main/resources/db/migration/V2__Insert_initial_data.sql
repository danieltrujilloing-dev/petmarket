-- =============================================================================
-- MIGRACIÓN V2: Insertar datos iniciales para PetMarket
-- =============================================================================

-- Insertar clientes de ejemplo
INSERT INTO clientes (nombre, email, telefono, direccion) VALUES 
('Juan Pérez', 'juan.perez@email.com', '+57 300 123 4567', 'Carrera 15 #45-67, Bogotá'),
('María García', 'maria.garcia@email.com', '+57 301 234 5678', 'Calle 80 #12-34, Medellín'),
('Carlos Rodríguez', 'carlos.rodriguez@email.com', '+57 302 345 6789', 'Avenida 6 #23-45, Cali'),
('Ana Martínez', 'ana.martinez@email.com', '+57 303 456 7890', 'Carrera 7 #56-78, Barranquilla'),
('Luis Fernández', 'luis.fernandez@email.com', '+57 304 567 8901', 'Calle 100 #89-01, Cartagena');

-- Insertar productos para perros (ALIMENTO)
INSERT INTO productos (nombre, tipo, especie_destino, precio, descripcion, imagen_url, atributos) VALUES 
('Purina Pro Plan Adult Dog', 'ALIMENTO', 'PERRO', 89900.00, 'Alimento premium para perros adultos con pollo y arroz', 'https://example.com/images/purina-pro-plan.jpg', '{"peso_kg": 15, "sabor": "pollo", "edad": "adulto", "raza": "todas"}'),
('Royal Canin Puppy', 'ALIMENTO', 'PERRO', 125000.00, 'Alimento especializado para cachorros hasta 12 meses', 'https://example.com/images/royal-canin-puppy.jpg', '{"peso_kg": 8, "sabor": "pollo", "edad": "cachorro", "beneficios": ["digestión", "inmunidad"]}'),
('Hills Science Diet Senior', 'ALIMENTO', 'PERRO', 95500.00, 'Alimento para perros mayores de 7 años', 'https://example.com/images/hills-senior.jpg', '{"peso_kg": 12, "sabor": "cordero", "edad": "senior", "beneficios": ["articulaciones", "cognición"]}'),

-- Insertar productos para gatos (ALIMENTO)
('Whiskas Adult Cat', 'ALIMENTO', 'GATO', 45800.00, 'Alimento completo para gatos adultos con atún', 'https://example.com/images/whiskas-adult.jpg', '{"peso_kg": 3, "sabor": "atún", "edad": "adulto", "textura": "croquetas"}'),
('Cat Chow Kitten', 'ALIMENTO', 'GATO', 38500.00, 'Alimento para gatitos de 0 a 12 meses', 'https://example.com/images/cat-chow-kitten.jpg', '{"peso_kg": 2, "sabor": "pollo", "edad": "gatito", "beneficios": ["crecimiento", "desarrollo"]}'),

-- Insertar accesorios para perros
('Collar Ajustable Premium', 'ACCESORIO', 'PERRO', 25000.00, 'Collar ajustable de nylon con hebilla metálica', 'https://example.com/images/collar-premium.jpg', '{"material": "nylon", "tallas": ["S", "M", "L"], "colores": ["negro", "azul", "rojo"]}'),
('Correa Retráctil 5m', 'ACCESORIO', 'PERRO', 65000.00, 'Correa retráctil con sistema de frenado automático', 'https://example.com/images/correa-retractil.jpg', '{"longitud_m": 5, "peso_max_kg": 25, "material": "nylon"}'),
('Cama Ortopédica Grande', 'ACCESORIO', 'PERRO', 150000.00, 'Cama ortopédica con espuma de memoria para perros grandes', 'https://example.com/images/cama-ortopedica.jpg', '{"tamaño": "L", "dimensiones": "80x60cm", "material": "espuma_memoria", "lavable": true}'),

-- Insertar accesorios para gatos
('Rascador Torre 120cm', 'ACCESORIO', 'GATO', 180000.00, 'Torre rascador con múltiples niveles y juguetes', 'https://example.com/images/rascador-torre.jpg', '{"altura_cm": 120, "niveles": 4, "material": "sisal", "incluye": ["cueva", "juguetes"]}'),
('Arenera Autolimpiante', 'ACCESORIO', 'GATO', 350000.00, 'Sistema de arenera automática con sensor de movimiento', 'https://example.com/images/arenera-auto.jpg', '{"tipo": "automatica", "capacidad_l": 10, "sensor": true, "filtro_carbono": true}'),

-- Insertar productos para aves
('Alimento Premium Aves', 'ALIMENTO', 'AVE', 28000.00, 'Mezcla de semillas premium para aves pequeñas', 'https://example.com/images/alimento-aves.jpg', '{"peso_kg": 1, "tipo_ave": "pequeñas", "ingredientes": ["alpiste", "mijo", "avena"]}'),
('Jaula Grande Aves', 'ACCESORIO', 'AVE', 220000.00, 'Jaula espaciosa con accesorios incluidos', 'https://example.com/images/jaula-aves.jpg', '{"dimensiones": "60x40x80cm", "material": "acero", "incluye": ["perchas", "comederos", "bebedero"]}');

-- Insertar inventario para los productos
INSERT INTO inventario (producto_id, stock_disponible, umbral_reposicion) VALUES 
(1, 45, 10),   -- Purina Pro Plan
(2, 30, 8),    -- Royal Canin Puppy
(3, 25, 5),    -- Hills Senior
(4, 60, 15),   -- Whiskas Adult
(5, 40, 12),   -- Cat Chow Kitten
(6, 100, 20),  -- Collar Premium
(7, 35, 8),    -- Correa Retráctil
(8, 15, 3),    -- Cama Ortopédica
(9, 8, 2),     -- Rascador Torre
(10, 5, 1),    -- Arenera Autolimpiante
(11, 80, 20),  -- Alimento Aves
(12, 12, 3);   -- Jaula Aves

-- Crear algunos carritos con items
INSERT INTO carritos (cliente_id) VALUES (1), (2), (3);

-- Items en el carrito del cliente 1 (Juan)
INSERT INTO carrito_items (carrito_id, producto_id, cantidad, precio_unitario) VALUES 
(1, 1, 2, 89900.00),   -- 2 Purina Pro Plan
(1, 6, 1, 25000.00);   -- 1 Collar Premium

-- Items en el carrito del cliente 2 (María)
INSERT INTO carrito_items (carrito_id, producto_id, cantidad, precio_unitario) VALUES 
(2, 4, 3, 45800.00),   -- 3 Whiskas Adult
(2, 9, 1, 180000.00);  -- 1 Rascador Torre

-- Crear algunos pedidos de ejemplo
INSERT INTO pedidos (cliente_id, total, estado, direccion_envio, notas) VALUES 
(4, 315000.00, 'ENVIADO', 'Carrera 7 #56-78, Barranquilla', 'Entregar en horario de mañana'),
(5, 500000.00, 'EN_PREPARACION', 'Calle 100 #89-01, Cartagena', 'Confirmar entrega por teléfono');

-- Items del primer pedido (Ana - ID 4)
INSERT INTO pedido_items (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(1, 2, 2, 125000.00, 250000.00),  -- 2 Royal Canin Puppy
(1, 7, 1, 65000.00, 65000.00);    -- 1 Correa Retráctil

-- Items del segundo pedido (Luis - ID 5)
INSERT INTO pedido_items (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(2, 8, 1, 150000.00, 150000.00),  -- 1 Cama Ortopédica
(2, 10, 1, 350000.00, 350000.00); -- 1 Arenera Autolimpiante

-- Insertar algunas solicitudes de adopción
INSERT INTO solicitudes_adopcion (cliente_id, nombre_solicitante, email_solicitante, telefono_solicitante, tipo_mascota_deseada, motivo_adopcion, experiencia_previa, situacion_vivienda, estado, observaciones_refugio) VALUES 
(1, 'Juan Pérez', 'juan.perez@email.com', '+57 300 123 4567', 'PERRO', 'Familia con experiencia en perros grandes. Casa con jardín.', 'Tuve perros durante 10 años', 'Casa con jardín amplio', 'EN_VERIFICACION', 'Candidato prometedor'),
(2, 'María García', 'maria.garcia@email.com', '+57 301 234 5678', 'GATO', 'Perfecto hogar para gato senior. Seguimiento veterinario completo.', 'Experiencia con gatos mayores', 'Apartamento tranquilo', 'APROBADA', 'Excelente perfil para adopción'),
(3, 'Carlos Rodríguez', 'carlos.rodriguez@email.com', '+57 302 345 6789', 'CONEJO', 'Interesado en adoptar pareja de conejos', 'Primera vez con conejos', 'Casa con patio', 'PENDIENTE', NULL),
(4, 'Ana Martínez', 'ana.martinez@email.com', '+57 303 456 7890', 'PERRO', 'Busco compañía para mi hijo', 'Sin experiencia previa', 'Apartamento pequeño', 'RECHAZADA', 'No cumple con los requisitos de espacio mínimo para la especie');