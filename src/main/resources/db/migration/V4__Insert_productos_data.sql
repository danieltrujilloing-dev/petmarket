-- Migración para insertar datos de productos de prueba
-- Versión: 4
-- Descripción: Insert sample product data

-- Productos para perros
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Collar Básico para Perro', 'Collar ajustable de nylon resistente para perros medianos y grandes', 15.99, 'ACCESORIO', 'PERRO', 50, true, 'https://example.com/collar-basico-perro.jpg'),
('Correa Retráctil Premium', 'Correa retráctil de 5 metros con sistema de freno automático', 45.99, 'ACCESORIO', 'PERRO', 25, true, 'https://example.com/correa-retractil.jpg'),
('Alimento Premium Adulto', 'Alimento balanceado para perros adultos con pollo y arroz', 89.99, 'ALIMENTO', 'PERRO', 100, true, 'https://example.com/alimento-premium-adulto.jpg'),
('Juguete Pelota de Goma', 'Pelota resistente de goma natural para ejercicio y diversión', 12.50, 'ACCESORIO', 'PERRO', 75, true, 'https://example.com/pelota-goma.jpg'),
('Cama Ortopédica Grande', 'Cama ortopédica con memoria de forma para perros grandes', 199.99, 'ACCESORIO', 'PERRO', 15, true, 'https://example.com/cama-ortopedica.jpg'),
('Champú Antipulgas', 'Champú medicado para eliminar pulgas y garrapatas', 24.99, 'ACCESORIO', 'PERRO', 40, true, 'https://example.com/champu-antipulgas.jpg');

-- Productos para gatos
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Arena Sanitaria Premium', 'Arena aglomerante con control de olores de larga duración', 18.99, 'ACCESORIO', 'GATO', 80, true, 'https://example.com/arena-sanitaria.jpg'),
('Rascador Torre Gigante', 'Rascador de sisal natural con múltiples niveles y juguetes', 159.99, 'ACCESORIO', 'GATO', 12, true, 'https://example.com/rascador-torre.jpg'),
('Alimento Gatitos Kitten', 'Alimento especial para gatitos de 2 a 12 meses con DHA', 34.99, 'ALIMENTO', 'GATO', 60, true, 'https://example.com/alimento-kitten.jpg'),
('Ratón Interactivo LED', 'Juguete ratón con luces LED y movimiento automático', 29.99, 'ACCESORIO', 'GATO', 35, true, 'https://example.com/raton-led.jpg'),
('Transportadora Airline', 'Transportadora aprobada para viajes en avión', 89.99, 'ACCESORIO', 'GATO', 20, true, 'https://example.com/transportadora-airline.jpg');

-- Productos para aves
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Semillas Mix Canarios', 'Mezcla de semillas premium para canarios y jilgueros', 12.99, 'ALIMENTO', 'AVE', 45, true, 'https://example.com/semillas-canarios.jpg'),
('Jaula Decorativa Grande', 'Jaula espaciosa con comederos y bebederos incluidos', 129.99, 'ACCESORIO', 'AVE', 8, true, 'https://example.com/jaula-decorativa.jpg'),
('Suplemento Vitamínico', 'Vitaminas y minerales esenciales para aves domésticas', 19.99, 'ACCESORIO', 'AVE', 30, true, 'https://example.com/vitaminas-aves.jpg');

-- Productos para peces
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Alimento Escamas Tropicales', 'Escamas nutritivas para peces tropicales de agua dulce', 8.99, 'ALIMENTO', 'PEZ', 90, true, 'https://example.com/escamas-tropicales.jpg'),
('Filtro Acuario 100L', 'Sistema de filtración completo para acuarios hasta 100 litros', 75.99, 'ACCESORIO', 'PEZ', 18, true, 'https://example.com/filtro-acuario.jpg'),
('Decoración Coral Artificial', 'Coral decorativo seguro para acuarios marinos', 22.50, 'ACCESORIO', 'PEZ', 25, true, 'https://example.com/coral-artificial.jpg');

-- Productos universales
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Desinfectante Universal', 'Desinfectante seguro para todas las especies de mascotas', 16.99, 'ACCESORIO', 'UNIVERSAL', 55, true, 'https://example.com/desinfectante-universal.jpg'),
('Comedero Automático', 'Comedero programable para múltiples especies', 99.99, 'ACCESORIO', 'UNIVERSAL', 22, true, 'https://example.com/comedero-automatico.jpg'),
('Kit Primeros Auxilios', 'Kit básico de primeros auxilios para mascotas', 39.99, 'ACCESORIO', 'UNIVERSAL', 30, true, 'https://example.com/kit-primeros-auxilios.jpg');

-- Productos para roedores
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Pellets Hámster Premium', 'Pellets balanceados para hámsters y otros roedores pequeños', 6.99, 'ALIMENTO', 'ROEDOR', 70, true, 'https://example.com/pellets-hamster.jpg'),
('Jaula Multi-nivel', 'Jaula espaciosa con múltiples niveles y accesorios', 84.99, 'ACCESORIO', 'ROEDOR', 15, true, 'https://example.com/jaula-multinivel.jpg'),
('Rueda de Ejercicio', 'Rueda silenciosa para ejercicio de roedores', 18.99, 'ACCESORIO', 'ROEDOR', 40, true, 'https://example.com/rueda-ejercicio.jpg');

-- Productos para conejos
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Heno Timothy Premium', 'Heno de timothy de primera calidad para conejos', 14.99, 'ALIMENTO', 'CONEJO', 35, true, 'https://example.com/heno-timothy.jpg'),
('Casa de Madera Natural', 'Refugio de madera natural sin tratamientos químicos', 49.99, 'ACCESORIO', 'CONEJO', 12, true, 'https://example.com/casa-madera.jpg');

-- Productos para reptiles
INSERT INTO productos (nombre, descripcion, precio, tipo, especie_destino, stock, activo, imagen_url) VALUES
('Lámpara UV Reptiles', 'Lámpara UV completa para terrarios de reptiles', 89.99, 'ACCESORIO', 'REPTIL', 18, true, 'https://example.com/lampara-uv.jpg'),
('Sustrato Fibra de Coco', 'Sustrato natural de fibra de coco para terrarios', 11.99, 'ACCESORIO', 'REPTIL', 28, true, 'https://example.com/sustrato-coco.jpg'),
('Alimento Gecko Premium', 'Alimento completo para geckos y otros reptiles pequeños', 26.99, 'ALIMENTO', 'REPTIL', 22, true, 'https://example.com/alimento-gecko.jpg');

-- Actualizar las fechas para simular productos creados en diferentes momentos
UPDATE productos SET created_at = created_at - INTERVAL '30 days' WHERE id BETWEEN 1 AND 5;
UPDATE productos SET created_at = created_at - INTERVAL '15 days' WHERE id BETWEEN 6 AND 10;
UPDATE productos SET created_at = created_at - INTERVAL '7 days' WHERE id BETWEEN 11 AND 15;
UPDATE productos SET created_at = created_at - INTERVAL '3 days' WHERE id BETWEEN 16 AND 20;

-- Algunos productos sin stock para probar filtros
UPDATE productos SET stock = 0 WHERE id IN (8, 13, 19);

-- Un producto desactivado para probar filtros
UPDATE productos SET activo = false WHERE id = 21;
