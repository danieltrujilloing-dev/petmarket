# 📦 Caso de Uso: Inventario (Event-Driven)

## 🎯 Funcionalidades Implementadas

### **1. Gestión de Stock**
- ✅ **Consultar inventario por producto** con stock disponible
- ✅ **Actualizar stock manualmente** con motivos de cambio
- ✅ **Ajustar umbral de reposición** por producto
- ✅ **Listar inventarios con stock bajo** (< umbral)

### **2. Confirmación de Stock por Pago**
- ✅ **Al pagar un pedido, confirma la disminución definitiva de stock**
- ✅ **Validación de stock reservado** vs stock disponible
- ✅ **Actualización transaccional** de inventario
- ✅ **Publicación de evento StockConfirmedEvent** (Kafka)

### **3. Sistema de Alertas de Stock Bajo**
- ✅ **Si stock < umbral, emite evento LowStock** (Kafka)
- ✅ **Detección automática** en actualizaciones de stock
- ✅ **Validación de umbrales** configurables por producto

### **4. Tareas de Reposición Asíncronas**
- ✅ **Consumidor asíncrono de LowStock** crea tareas automáticamente
- ✅ **Persistencia en PostgreSQL** (justificación: consistencia transaccional)
- ✅ **Cálculo inteligente de cantidad sugerida** (3x umbral)
- ✅ **Priorización automática** basada en criticidad del stock
- ✅ **Estados de tarea**: PENDIENTE → EN_PROCESO → COMPLETADA → CANCELADA

## 🏗️ Arquitectura Event-Driven

### **Flujo de Eventos**
```
1. Pedido → PAGADO (estado)
2. InventarioApplicationService → confirmarStockPorPago()
3. Inventario.confirmarStock() → Actualiza stock definitivo
4. Si stock < umbral → LowStockEvent (Kafka)
5. LowStockEventConsumer → Procesa asíncronamente
6. TareaReposicionApplicationService → Crear tarea automática
7. Notificación → Log estructurado de tarea creada
```

### **Justificación: PostgreSQL vs NoSQL**
**Decisión: PostgreSQL para Tareas de Reposición**

**Razones:**
1. **Consistencia ACID**: Las tareas deben ser consistentes con el inventario
2. **Transacciones**: Crear tarea + actualizar inventario en una transacción
3. **Relaciones**: FK con productos, auditoría integrada
4. **Consultas complejas**: Reportes por prioridad, estado, fechas
5. **Simplicidad**: Una sola base de datos, menos complejidad operacional
6. **Volumen**: Las tareas no requieren escalabilidad masiva como NoSQL

## 📡 API REST Endpoints

### **Inventario**
```bash
# Obtener inventario de un producto
GET /api/v1/inventarios/producto/{productoId}

# Obtener inventarios con stock bajo
GET /api/v1/inventarios/stock-bajo

# Actualizar stock de un producto
PUT /api/v1/inventarios/producto/{productoId}/stock
{
  "nuevaCantidad": 25,
  "motivo": "Reposición de inventario"
}

# Ajustar umbral de reposición
PUT /api/v1/inventarios/producto/{productoId}/umbral?nuevoUmbral=10

# Confirmar stock por pago de pedido
POST /api/v1/inventarios/confirmar-stock/pedido/{pedidoId}
```

### **Tareas de Reposición**
```bash
# Obtener tarea por ID
GET /api/v1/tareas-reposicion/{tareaId}

# Obtener tareas pendientes
GET /api/v1/tareas-reposicion/pendientes

# Obtener tareas por estado
GET /api/v1/tareas-reposicion/estado/{estado}
# Estados: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA

# Obtener tareas por prioridad
GET /api/v1/tareas-reposicion/prioridad/{prioridad}
# Prioridades: BAJA, MEDIA, ALTA, CRITICA

# Obtener tareas vencidas
GET /api/v1/tareas-reposicion/vencidas

# Marcar tarea en proceso
PUT /api/v1/tareas-reposicion/{tareaId}/en-proceso

# Completar tarea
PUT /api/v1/tareas-reposicion/{tareaId}/completar
{
  "observacionesFinales": "Tarea completada exitosamente. Stock repuesto."
}

# Obtener estadísticas de tareas
GET /api/v1/tareas-reposicion/estadisticas
```

## 🔄 Eventos de Kafka

### **LowStockEvent**
```json
{
  "eventId": "uuid-123",
  "occurredOn": "2025-08-30T20:00:00Z",
  "productoId": 15,
  "stockActual": 2,
  "umbralReposicion": 10,
  "nombreProducto": "Purina Pro Plan Adulto",
  "tipoProducto": "ALIMENTO",
  "especieDestino": "PERRO"
}
```

### **StockConfirmedEvent**
```json
{
  "eventId": "uuid-456",
  "occurredOn": "2025-08-30T20:02:00Z",
  "pedidoId": 123,
  "clienteId": 456,
  "items": [
    {
      "productoId": 15,
      "cantidad": 2,
      "stockAnterior": 12,
      "stockActual": 10
    }
  ],
  "totalConfirmado": 89.99
}
```

### **TaskCreatedEvent**
```json
{
  "eventId": "uuid-789",
  "occurredOn": "2025-08-30T20:03:00Z",
  "tareaId": 1,
  "productoId": 15,
  "cantidadSugerida": 30,
  "prioridad": "ALTA",
  "motivoCreacion": "Stock bajo detectado automáticamente"
}
```

## 🎯 Reglas de Negocio Implementadas

### **Gestión de Stock**
- ✅ **Stock no puede ser negativo** (validación en dominio)
- ✅ **Umbral de reposición >= 0** (validación)
- ✅ **Motivo obligatorio** para cambios de stock
- ✅ **Auditoría completa** de cambios de inventario

### **Confirmación por Pago**
- ✅ **Solo pedidos PAGADOS** pueden confirmar stock
- ✅ **Validación de stock reservado** vs disponible
- ✅ **Transaccional**: Falla todo si un item no tiene stock
- ✅ **Idempotente**: No permite confirmar dos veces el mismo pedido

### **Detección de Stock Bajo**
- ✅ **Evaluación automática** en cada actualización de stock
- ✅ **Solo productos activos** generan alertas
- ✅ **Umbral configurable** por producto
- ✅ **Evento único** por producto (no spam de eventos)

### **Tareas de Reposición**
- ✅ **Creación automática** por eventos LowStock
- ✅ **Cantidad sugerida inteligente**: 3x umbral de reposición
- ✅ **Priorización automática**:
  - CRITICA: stock = 0
  - ALTA: stock < 25% umbral
  - MEDIA: stock < 50% umbral
  - BAJA: stock < 75% umbral
- ✅ **Fecha límite automática**: +7 días para CRITICA, +14 días para otras
- ✅ **Estados controlados**: Solo transiciones válidas

## 🗃️ Base de Datos

### **Tabla `inventario`**
```sql
CREATE TABLE inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE,
    stock_disponible INTEGER NOT NULL DEFAULT 0 CHECK (stock_disponible >= 0),
    stock_reservado INTEGER NOT NULL DEFAULT 0 CHECK (stock_reservado >= 0),
    umbral_reposicion INTEGER NOT NULL DEFAULT 5 CHECK (umbral_reposicion >= 0),
    fecha_ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);
```

### **Tabla `tareas_reposicion`**
```sql
CREATE TABLE tareas_reposicion (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad_sugerida INTEGER NOT NULL CHECK (cantidad_sugerida > 0),
    prioridad VARCHAR(10) NOT NULL CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')),
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA')),
    motivo_creacion TEXT NOT NULL,
    observaciones_finales TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP NOT NULL,
    fecha_completada TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);
```

### **Índices para Performance**
```sql
-- Inventario
CREATE INDEX idx_inventario_producto ON inventario(producto_id);
CREATE INDEX idx_inventario_stock_bajo ON inventario(stock_disponible, umbral_reposicion) 
WHERE stock_disponible < umbral_reposicion;

-- Tareas de Reposición
CREATE INDEX idx_tarea_estado ON tareas_reposicion(estado);
CREATE INDEX idx_tarea_prioridad ON tareas_reposicion(prioridad);
CREATE INDEX idx_tarea_producto ON tareas_reposicion(producto_id);
CREATE INDEX idx_tarea_fecha_limite ON tareas_reposicion(fecha_limite);
CREATE INDEX idx_tarea_vencidas ON tareas_reposicion(fecha_limite) 
WHERE estado IN ('PENDIENTE', 'EN_PROCESO') AND fecha_limite < CURRENT_TIMESTAMP;
```

## ⚙️ Configuración de Kafka

### **Topics**
```properties
# Configuración de topics en application.properties
kafka.topics.low-stock=petmarket.lowstockevent
kafka.topics.stock-confirmed=petmarket.stockconfirmedevent
kafka.topics.task-created=petmarket.taskcreatedevent

# Consumer Groups
kafka.consumer-groups.inventory-management=inventory-management-group
kafka.consumer-groups.task-management=task-management-group
```

### **Configuración de Consumidores**
```java
@KafkaListener(
    topics = "${kafka.topics.low-stock}", 
    groupId = "${kafka.consumer-groups.inventory-management}"
)
public void handleLowStock(String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
    // Procesamiento asíncrono de stock bajo con acknowledgment manual
}
```

## 🧪 Testing Completo

### **Tests Unitarios Implementados**
```bash
# Ejecutar tests del módulo de inventario
./gradlew test --tests "*Inventario*"
./gradlew test --tests "*TareaReposicion*"
```

**Tests implementados:**
- ✅ `InventarioApplicationServiceTest` - Tests de servicio de aplicación
- ✅ `InventarioTest` - Tests de entidad de dominio
- ✅ `TareaReposicionTest` - Tests de entidad de tareas
- ✅ `LowStockEventConsumerTest` - Tests de consumidor de eventos

### **Características de Testing**
- ✅ **JUnit 5** con `@Nested`, `@ParameterizedTest`, `@DisplayName`
- ✅ **Mockito** para mocking de dependencias
- ✅ **AssertJ** para assertions fluidas
- ✅ **Test Doubles** para aislamiento de dependencias
- ✅ **AAA Pattern** (Arrange, Act, Assert)

## 🚀 Cómo Probar la Implementación

### **1. Iniciar Servicios**
```bash
# Iniciar todos los servicios (PostgreSQL, Redis, Kafka, Zookeeper)
docker-compose up -d

# Ejecutar aplicación
./gradlew bootRun
```

### **2. Simular Stock Bajo**
```bash
# 1. Verificar inventario actual
curl "http://localhost:8080/api/v1/inventarios/producto/15"

# 2. Actualizar stock a cantidad crítica (menor al umbral)
curl -X PUT "http://localhost:8080/api/v1/inventarios/producto/15/stock" \
  -H "Content-Type: application/json" \
  -d '{
    "nuevaCantidad": 2,
    "motivo": "Simulación de stock crítico para testing"
  }'

# 3. Verificar que se generó evento LowStock (revisar logs)
tail -f logs/petmarket.log | grep -i "low.*stock"

# 4. Verificar que se creó tarea de reposición automáticamente
curl "http://localhost:8080/api/v1/tareas-reposicion/pendientes"
```

### **3. Confirmar Stock por Pago**
```bash
# 1. Crear pedido y cambiar estado a PAGADO
curl -X POST "http://localhost:8080/api/v1/pedidos/checkout/cliente/1"
curl -X PATCH "http://localhost:8080/api/v1/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -d '{"estado": "PAGADO"}'

# 2. Confirmar stock definitivo
curl -X POST "http://localhost:8080/api/v1/inventarios/confirmar-stock/pedido/1"

# 3. Verificar actualización de inventario
curl "http://localhost:8080/api/v1/inventarios/producto/15"
```

### **4. Gestionar Tareas de Reposición**
```bash
# 1. Listar tareas por prioridad
curl "http://localhost:8080/api/v1/tareas-reposicion/prioridad/CRITICA"

# 2. Marcar tarea en proceso
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/en-proceso"

# 3. Completar tarea
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/completar" \
  -H "Content-Type: application/json" \
  -d '{
    "observacionesFinales": "Stock repuesto exitosamente. Proveedor: PetSupplies Inc."
  }'

# 4. Ver estadísticas
curl "http://localhost:8080/api/v1/tareas-reposicion/estadisticas"
```

## 📊 Métricas y Monitoreo

### **Logs Estructurados**
- ✅ **Actualizaciones de stock**: Con cantidades anterior/nueva
- ✅ **Eventos LowStock**: Con detalles del producto y umbral
- ✅ **Confirmaciones de pago**: Con items y cantidades confirmadas
- ✅ **Creación de tareas**: Con prioridad y cantidad sugerida

### **Ejemplos de Logs**
```
📦 Actualizando stock del producto 15: 12 → 2 (Motivo: Stock crítico)
⚠️  Stock bajo detectado para producto 15: stock=2, umbral=10
📋 Tarea de reposición creada automáticamente: ID=1, Producto=15, Cantidad=30, Prioridad=ALTA
✅ Stock confirmado por pago del pedido 123: 2 items procesados
```

### **Métricas de Negocio**
- **Productos con stock bajo**: Consulta en tiempo real
- **Tareas pendientes por prioridad**: Dashboard de gestión
- **Tiempo promedio de reposición**: KPI operacional
- **Eventos de stock procesados**: Métricas de throughput

## 🎉 Resumen del Caso de Uso

### ✅ **Completamente Implementado:**

1. **📦 Gestión completa de inventario**
   - Consultar, actualizar stock, ajustar umbrales
   - Validaciones de negocio robustas
   - Auditoría completa de cambios

2. **💰 Confirmación de stock por pago**
   - Integración con ciclo de vida de pedidos
   - Transacciones ACID para consistencia
   - Eventos de confirmación para auditoría

3. **⚠️ Sistema de alertas automático**
   - Detección inteligente de stock bajo
   - Eventos Kafka para desacoplamiento
   - Configuración flexible de umbrales

4. **📋 Tareas de reposición asíncronas**
   - Creación automática por eventos
   - Priorización inteligente
   - Gestión completa del ciclo de vida

5. **🏗️ Arquitectura event-driven**
   - Desacoplamiento completo de componentes
   - Procesamiento asíncrono escalable
   - Consistencia eventual con PostgreSQL

6. **🧪 Testing y monitoreo**
   - Tests unitarios completos
   - Logs estructurados para debugging
   - Métricas de negocio en tiempo real

**¡El caso de uso de inventario está 100% funcional con gestión automática de stock y tareas de reposición! 🎯**
