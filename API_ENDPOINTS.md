# 📋 API Endpoints - Petmarket Application

Esta documentación lista todos los endpoints REST disponibles en la aplicación Petmarket con sus respectivos comandos curl para testing.

## 🏠 Base URL
```
http://localhost:8080
```

---

## 🛍️ Productos (Catálogo)

### 1. Listar todos los productos activos
```bash
# GET /api/v1/productos
curl -X GET "http://localhost:8080/api/v1/productos" \
  -H "Content-Type: application/json"
```

### 2. Buscar productos con filtros
```bash
# GET /api/v1/productos/buscar
curl -X GET "http://localhost:8080/api/v1/productos/buscar?tipos=ALIMENTO,ACCESORIO&especies=PERRO&precioMin=10.00&precioMax=100.00&q=collar" \
  -H "Content-Type: application/json"
```

### 3. Listar productos con paginación
```bash
# GET /api/v1/productos (con paginación)
curl -X GET "http://localhost:8080/api/v1/productos?page=0&size=5" \
  -H "Content-Type: application/json"
```

### 4. Obtener producto por ID
```bash
# GET /api/v1/productos/{id}
curl -X GET "http://localhost:8080/api/v1/productos/1" \
  -H "Content-Type: application/json"
```

### 5. Crear nuevo producto
```bash
# POST /api/v1/productos
curl -X POST "http://localhost:8080/api/v1/productos" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Collar Premium para Perros",
    "descripcion": "Collar de cuero genuino con hebilla de acero inoxidable",
    "precio": 45.99,
    "tipo": "ACCESORIO",
    "especie": "PERRO",
    "stock": 25,
    "activo": true,
    "imagenUrl": "https://example.com/collar-premium.jpg"
  }'
```

### 6. Actualizar producto
```bash
# PUT /api/v1/productos/{id}
curl -X PUT "http://localhost:8080/api/v1/productos/1" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Collar Premium para Perros - Actualizado",
    "descripcion": "Collar de cuero genuino con hebilla de acero inoxidable - Versión mejorada",
    "precio": 49.99,
    "tipo": "ACCESORIO",
    "especie": "PERRO",
    "stock": 30,
    "activo": true,
    "imagenUrl": "https://example.com/collar-premium-v2.jpg"
  }'
```

### 7. Eliminar producto (soft delete)
```bash
# DELETE /api/v1/productos/{id}
curl -X DELETE "http://localhost:8080/api/v1/productos/1" \
  -H "Content-Type: application/json"
```

### 8. Obtener tipos de producto disponibles
```bash
# GET /api/v1/productos/tipos
curl -X GET "http://localhost:8080/api/v1/productos/tipos" \
  -H "Content-Type: application/json"
```

### 9. Obtener especies disponibles
```bash
# GET /api/v1/productos/especies
curl -X GET "http://localhost:8080/api/v1/productos/especies" \
  -H "Content-Type: application/json"
```

### 10. Obtener estadísticas del cache
```bash
# GET /api/v1/productos/cache/stats
curl -X GET "http://localhost:8080/api/v1/productos/cache/stats" \
  -H "Content-Type: application/json"
```

### 11. Invalidar cache manualmente
```bash
# DELETE /api/v1/productos/cache
curl -X DELETE "http://localhost:8080/api/v1/productos/cache" \
  -H "Content-Type: application/json"
```

---

## 🛒 Carrito de Compras

### 1. Obtener carrito de un cliente
```bash
# GET /api/v1/carritos/cliente/{clienteId}
curl -X GET "http://localhost:8080/api/v1/carritos/cliente/1" \
  -H "Content-Type: application/json"
```

### 2. Agregar item al carrito
```bash
# POST /api/v1/carritos/cliente/{clienteId}/items
curl -X POST "http://localhost:8080/api/v1/carritos/cliente/1/items" \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": 15,
    "cantidad": 2
  }'
```

### 3. Actualizar cantidad de un item
```bash
# PUT /api/v1/carritos/cliente/{clienteId}/items/{productoId}
curl -X PUT "http://localhost:8080/api/v1/carritos/cliente/1/items/15" \
  -H "Content-Type: application/json" \
  -d '{
    "cantidad": 3
  }'
```

### 4. Eliminar item del carrito
```bash
# DELETE /api/v1/carritos/cliente/{clienteId}/items/{productoId}
curl -X DELETE "http://localhost:8080/api/v1/carritos/cliente/1/items/15" \
  -H "Content-Type: application/json"
```

### 5. Limpiar carrito completamente
```bash
# DELETE /api/v1/carritos/cliente/{clienteId}
curl -X DELETE "http://localhost:8080/api/v1/carritos/cliente/1" \
  -H "Content-Type: application/json"
```

---

## 📦 Pedidos

### 1. Realizar checkout (crear pedido)
```bash
# POST /api/v1/pedidos/checkout/cliente/{clienteId}
curl -X POST "http://localhost:8080/api/v1/pedidos/checkout/cliente/1" \
  -H "Content-Type: application/json"
```

### 2. Obtener pedido por ID
```bash
# GET /api/v1/pedidos/{pedidoId}
curl -X GET "http://localhost:8080/api/v1/pedidos/1" \
  -H "Content-Type: application/json"
```

### 3. Cancelar pedido
```bash
# PATCH /api/v1/pedidos/{pedidoId}/cancelar
curl -X PATCH "http://localhost:8080/api/v1/pedidos/1/cancelar" \
  -H "Content-Type: application/json"
```

### 4. Cambiar estado del pedido
```bash
# PATCH /api/v1/pedidos/{pedidoId}/estado
curl -X PATCH "http://localhost:8080/api/v1/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "PAGADO"
  }'
```

---

## 📊 Inventario

### 1. Obtener inventario de un producto
```bash
# GET /api/v1/inventarios/producto/{productoId}
curl -X GET "http://localhost:8080/api/v1/inventarios/producto/15" \
  -H "Content-Type: application/json"
```

### 2. Obtener inventarios con stock bajo
```bash
# GET /api/v1/inventarios/stock-bajo
curl -X GET "http://localhost:8080/api/v1/inventarios/stock-bajo" \
  -H "Content-Type: application/json"
```

### 3. Actualizar stock de un producto
```bash
# PUT /api/v1/inventarios/producto/{productoId}/stock
curl -X PUT "http://localhost:8080/api/v1/inventarios/producto/15/stock" \
  -H "Content-Type: application/json" \
  -d '{
    "nuevaCantidad": 5,
    "motivo": "Reposición de inventario"
  }'
```

### 4. Ajustar umbral de reposición
```bash
# PUT /api/v1/inventarios/producto/{productoId}/umbral
curl -X PUT "http://localhost:8080/api/v1/inventarios/producto/15/umbral?nuevoUmbral=10" \
  -H "Content-Type: application/json"
```

### 5. Confirmar stock por pago de pedido
```bash
# POST /api/v1/inventarios/confirmar-stock/pedido/{pedidoId}
curl -X POST "http://localhost:8080/api/v1/inventarios/confirmar-stock/pedido/1" \
  -H "Content-Type: application/json"
```

---

## 📋 Tareas de Reposición

### 1. Obtener tarea por ID
```bash
# GET /api/v1/tareas-reposicion/{tareaId}
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/1" \
  -H "Content-Type: application/json"
```

### 2. Obtener tareas pendientes
```bash
# GET /api/v1/tareas-reposicion/pendientes
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/pendientes" \
  -H "Content-Type: application/json"
```

### 3. Obtener tareas por estado
```bash
# GET /api/v1/tareas-reposicion/estado/{estado}
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/estado/PENDIENTE" \
  -H "Content-Type: application/json"

# Estados disponibles: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA
```

### 4. Obtener tareas por prioridad
```bash
# GET /api/v1/tareas-reposicion/prioridad/{prioridad}
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/prioridad/CRITICA" \
  -H "Content-Type: application/json"

# Prioridades disponibles: BAJA, MEDIA, ALTA, CRITICA
```

### 5. Obtener tareas vencidas
```bash
# GET /api/v1/tareas-reposicion/vencidas
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/vencidas" \
  -H "Content-Type: application/json"
```

### 6. Marcar tarea en proceso
```bash
# PUT /api/v1/tareas-reposicion/{tareaId}/en-proceso
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/en-proceso" \
  -H "Content-Type: application/json"
```

### 7. Completar tarea
```bash
# PUT /api/v1/tareas-reposicion/{tareaId}/completar
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/completar" \
  -H "Content-Type: application/json" \
  -d '{
    "observacionesFinales": "Tarea completada exitosamente. Stock repuesto."
  }'
```

### 8. Obtener estadísticas de tareas
```bash
# GET /api/v1/tareas-reposicion/estadisticas
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/estadisticas" \
  -H "Content-Type: application/json"
```

---

## 🏥 Health Check & Actuator

### 1. Health Check
```bash
# GET /actuator/health
curl -X GET "http://localhost:8080/actuator/health" \
  -H "Content-Type: application/json"
```

### 2. Application Info
```bash
# GET /actuator/info
curl -X GET "http://localhost:8080/actuator/info" \
  -H "Content-Type: application/json"
```

---

## 🔧 Ejemplos de Flujos Completos

### Flujo 1: Agregar producto al carrito y hacer checkout
```bash
# 1. Agregar producto al carrito
curl -X POST "http://localhost:8080/api/v1/carritos/cliente/2/items" \
  -H "Content-Type: application/json" \
  -d '{"productoId": 15, "cantidad": 1}'

# 2. Ver carrito
curl -X GET "http://localhost:8080/api/v1/carritos/cliente/2"

# 3. Hacer checkout
curl -X POST "http://localhost:8080/api/v1/pedidos/checkout/cliente/2"
```

### Flujo 2: Gestionar stock bajo
```bash
# 1. Ver inventarios con stock bajo
curl -X GET "http://localhost:8080/api/v1/inventarios/stock-bajo"

# 2. Actualizar stock (esto puede generar evento LowStock)
curl -X PUT "http://localhost:8080/api/v1/inventarios/producto/15/stock" \
  -H "Content-Type: application/json" \
  -d '{"nuevaCantidad": 2, "motivo": "Stock crítico"}'

# 3. Ver tareas de reposición generadas automáticamente
curl -X GET "http://localhost:8080/api/v1/tareas-reposicion/pendientes"

# 4. Marcar tarea en proceso
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/en-proceso"

# 5. Completar tarea
curl -X PUT "http://localhost:8080/api/v1/tareas-reposicion/1/completar" \
  -H "Content-Type: application/json" \
  -d '{"observacionesFinales": "Reposición completada"}'
```

### Flujo 3: Confirmar pago y stock
```bash
# 1. Cambiar estado del pedido a PAGADO
curl -X PATCH "http://localhost:8080/api/v1/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -d '{"estado": "PAGADO"}'

# 2. Confirmar stock definitivo por pago
curl -X POST "http://localhost:8080/api/v1/inventarios/confirmar-stock/pedido/1"
```

---

## 📝 Notas Importantes

1. **Autenticación**: Los endpoints actuales no requieren autenticación, pero en producción deberían implementar JWT o similar.

2. **CORS**: Todos los controladores tienen CORS habilitado para `*` con `maxAge = 3600`.

3. **Eventos Kafka**: Algunos endpoints publican eventos automáticamente:
   - Agregar item al carrito → `CartItemAddedEvent`
   - Checkout → `OrderCreatedEvent`
   - Stock bajo → `LowStockEvent`
   - Confirmar stock → `StockConfirmedEvent`

4. **Cache Redis**: Los endpoints de productos utilizan cache con TTL configurable.

5. **Validaciones**: Los DTOs de request tienen validaciones Jakarta Bean Validation.

6. **Manejo de Errores**: Todos los controladores tienen `@ExceptionHandler` para manejo centralizado de errores.

7. **Logging**: Todos los endpoints logean información de entrada y salida para debugging.

---

## 🚀 Testing Rápido

Para probar rápidamente todos los endpoints principales:

```bash
# Script de testing básico
#!/bin/bash

echo "🔍 Testing Health Check..."
curl -s "http://localhost:8080/actuator/health" | jq .

echo "📦 Testing Products..."
curl -s "http://localhost:8080/api/v1/productos?page=0&size=3" | jq .

echo "🛒 Testing Cart..."
curl -s -X POST "http://localhost:8080/api/v1/carritos/cliente/1/items" \
  -H "Content-Type: application/json" \
  -d '{"productoId": 15, "cantidad": 1}' | jq .

echo "📊 Testing Inventory..."
curl -s "http://localhost:8080/api/v1/inventarios/stock-bajo" | jq .

echo "📋 Testing Tasks..."
curl -s "http://localhost:8080/api/v1/tareas-reposicion/pendientes" | jq .

echo "✅ All tests completed!"
```

---

*Documentación generada para Petmarket Application - Arquitectura Hexagonal con Spring Boot, PostgreSQL, Redis y Kafka*
