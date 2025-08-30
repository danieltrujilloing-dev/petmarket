# 🛒 PetMarket - Catálogo de Productos

Marketplace de productos para mascotas implementado con **Arquitectura Hexagonal**.

## 🎯 Casos de Uso Implementados

### **1. Catálogo de Productos**
- ✅ Listar productos con filtros (tipo, especie, rango de precio)
- ✅ Cache de lectura con TTL configurable
- ✅ Invalidación automática al crear/actualizar productos

### **2. Carrito y Pedido** 
- ✅ Agregar/quitar ítems al carrito (validaciones SOLID)
- ✅ Checkout: crea pedido, reserva stock, calcula total
- ✅ Strategy Pattern para pricing (precio base vs promo perro)
- ✅ Publica evento OrderCreated (Kafka)

## 🏗️ Arquitectura

```
├── domain/          # Lógica de negocio pura
├── application/     # Casos de uso
├── infrastructure/  # Adaptadores (PostgreSQL, Redis, Kafka)
└── web/            # Controladores REST
```

## 🔧 Tecnologías

- **Java 17** + **Spring Boot 3.5**
- **PostgreSQL** (base de datos principal)
- **Redis** (cache con TTL)
- **Kafka** (eventos de dominio)
- **JPA + Lombok** (persistencia)
- **JUnit 5** (tests)

## 🚀 Ejecutar

### Servicios
```bash
docker-compose up -d
```

### Aplicación
```bash
./gradlew bootRun
```

### Tests
```bash
./run-catalog-tests.sh
```

## 📡 API REST

### Catálogo (con cache)
```bash
GET /api/v1/productos                    # Todos los productos
GET /api/v1/productos/buscar?tipos=...   # Filtros avanzados
GET /api/v1/productos/tipo/Alimento      # Por tipo
GET /api/v1/productos/especie/Perro      # Por especie
```

### Carrito
```bash
GET    /api/v1/carritos/cliente/{id}           # Obtener carrito
POST   /api/v1/carritos/cliente/{id}/items     # Agregar item
PUT    /api/v1/carritos/cliente/{id}/items/{productId}  # Actualizar cantidad
DELETE /api/v1/carritos/cliente/{id}/items/{productId}  # Eliminar item
```

### Pedidos
```bash
POST  /api/v1/pedidos/checkout/cliente/{id}    # Checkout (crear pedido)
GET   /api/v1/pedidos/{id}                     # Obtener pedido
PATCH /api/v1/pedidos/{id}/cancelar            # Cancelar pedido
PATCH /api/v1/pedidos/{id}/estado              # Cambiar estado
```

## 📊 Entidades

- **Producto** (id, nombre, tipo, especie, precio, atributos, activo)
- **Inventario** (productoId, stock, umbralReposición)
- **Cliente** (id, nombre, email)
- **Carrito** (clienteId, ítems)
- **Pedido** (id, clienteId, ítems, total, estado)
- **SolicitudAdopción** (id, clienteId, mascotaIdExterna, estado)

## ⚙️ Configuración

### Cache TTL
```properties
petmarket.cache.productos.ttl=PT15M  # 15 minutos
```

### Perfiles de Test
- `unit-test`: Tests de dominio (sin Redis)
- `integration-test`: Tests con Redis + PostgreSQL
