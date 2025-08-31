# 🛒 PetMarket - Catálogo de Productos

Marketplace de productos para mascotas implementado con **Arquitectura Hexagonal**.

## 🎯 Casos de Uso Implementados

### **1. Catálogo**
- ✅ Listar productos con filtros (tipo, especie, rango de precio)
- ✅ Cache de lectura con TTL configurable (Redis)
- ✅ Invalidación automática al crear/actualizar productos

### **2. Carrito y Pedido**
- ✅ Agregar/quitar ítems al carrito (no cantidades negativas, validar existencia)
- ✅ Checkout: crea Pedido, reserva stock, calcula total
- ✅ Strategy Pattern para pricing ("precio base" vs "promo perro")
- ✅ Publica evento OrderCreated (Kafka)

### **3. Inventario**
- ✅ Al pagar pedido, confirma disminución definitiva de stock
- ✅ Si stock < umbral, emite evento LowStock (Kafka)
- ✅ Consumidor asíncrono crea "tarea de reposición" (PostgreSQL)

### **4. Adopciones (Event-Driven)**
- ✅ Endpoint crear SolicitudAdopción (síncrono) con validaciones
- ✅ Publica evento AdoptionRequested (Kafka)
- ✅ Worker asíncrono consume evento, llama servicio externo simulado
- ✅ Publica AdoptionApproved/AdoptionRejected según verificación
- ✅ Actualiza estado de solicitud basado en eventos
- ✅ Notificación simulada al cliente (logs estructurados)

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

### Inventario
```bash
GET   /api/v1/inventarios/producto/{id}        # Obtener inventario
GET   /api/v1/inventarios/stock-bajo           # Inventarios con stock bajo
PUT   /api/v1/inventarios/producto/{id}/stock  # Actualizar stock
POST  /api/v1/inventarios/confirmar-stock/pedido/{id} # Confirmar stock por pago
```

### Adopciones
```bash
POST  /api/v1/adopciones/solicitudes           # Crear solicitud
GET   /api/v1/adopciones/solicitudes/{id}      # Obtener solicitud
GET   /api/v1/adopciones/solicitudes/pendientes # Listar pendientes
GET   /api/v1/adopciones/solicitudes/cliente/{id} # Por cliente
PUT   /api/v1/adopciones/solicitudes/{id}/cancelar # Cancelar
```

## 📊 Entidades Implementadas

- **Producto** (id, nombre, tipo: alimento/accesorio, especie destino, precio, atributos, activo)
- **Inventario** (productoId, stockDisponible, umbralReposición)
- **Cliente** (id, nombre, email)
- **Carrito** (clienteId, ítems: productoId, cantidad)
- **Pedido** (id, clienteId, ítems, total, estado: CREADO | PAGADO | EN_PREPARACION | ENVIADO | CANCELADO)
- **SolicitudAdopción** (id, clienteId, tipoMascotaDeseada, estado: PENDIENTE | EN_VERIFICACION | APROBADA | RECHAZADA | CANCELADA)
- **TareaReposición** (id, productoId, cantidadSugerida, prioridad, estado, fechaCreación)

## ⚙️ Configuración

### Cache TTL
```properties
petmarket.cache.productos.ttl=PT15M  # 15 minutos
```

### Perfiles de Test
- `unit-test`: Tests de dominio (sin Redis)
- `integration-test`: Tests con Redis + PostgreSQL
