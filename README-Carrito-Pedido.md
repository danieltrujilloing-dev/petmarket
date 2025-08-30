# 🛒 Caso de Uso: Carrito y Pedido

## 🎯 Funcionalidades Implementadas

### **1. Gestión de Carrito**
- ✅ **Agregar ítems al carrito** con validaciones SOLID
- ✅ **Quitar ítems del carrito** 
- ✅ **Actualizar cantidades** (no permite cantidades negativas)
- ✅ **Validar existencia de productos** antes de agregar
- ✅ **Limpiar carrito completo**

### **2. Proceso de Checkout**
- ✅ **Validación de carrito no vacío**
- ✅ **Validación de stock disponible**
- ✅ **Reserva automática de stock** (disminuye inventario)
- ✅ **Cálculo de total con Strategy Pattern**
- ✅ **Creación de pedido**
- ✅ **Limpieza automática del carrito**
- ✅ **Publicación de evento OrderCreated** (Kafka)

### **3. Estrategias de Pricing (Strategy Pattern)**
- ✅ **Precio Base**: Sin descuentos
- ✅ **Promo Perro**: 15% descuento en productos para perros
- ✅ **Extensible**: Fácil agregar nuevas estrategias

## 🏗️ Arquitectura SOLID

### **Single Responsibility Principle (SRP)**
- `CarritoApplicationService`: Solo gestión de carrito
- `PedidoApplicationService`: Solo procesamiento de pedidos
- `PricingStrategy`: Solo cálculo de precios
- Cada clase tiene una única razón para cambiar

### **Open/Closed Principle (OCP)**
- `PricingStrategy`: Abierto para extensión, cerrado para modificación
- Nuevas estrategias sin modificar código existente
- Nuevos tipos de eventos sin cambiar el publisher

### **Liskov Substitution Principle (LSP)**
- Todas las implementaciones de `PricingStrategy` son intercambiables
- Los servicios implementan correctamente sus interfaces

### **Interface Segregation Principle (ISP)**
- `GestionarCarritoUseCase`: Solo métodos de carrito
- `ProcesarPedidoUseCase`: Solo métodos de pedido
- Interfaces específicas y cohesivas

### **Dependency Inversion Principle (DIP)**
- Servicios dependen de abstracciones (puertos)
- No dependen de implementaciones concretas
- Inversión de control completa

## 📡 API REST Endpoints

### **Carrito**
```bash
# Obtener carrito
GET /api/v1/carritos/cliente/{clienteId}

# Agregar item
POST /api/v1/carritos/cliente/{clienteId}/items
{
  "productoId": 1,
  "cantidad": 2
}

# Actualizar cantidad
PUT /api/v1/carritos/cliente/{clienteId}/items/{productoId}
{
  "cantidad": 3
}

# Eliminar item
DELETE /api/v1/carritos/cliente/{clienteId}/items/{productoId}

# Limpiar carrito
DELETE /api/v1/carritos/cliente/{clienteId}
```

### **Pedidos**
```bash
# Checkout (crear pedido)
POST /api/v1/pedidos/checkout/cliente/{clienteId}

# Obtener pedido
GET /api/v1/pedidos/{pedidoId}

# Cancelar pedido
PATCH /api/v1/pedidos/{pedidoId}/cancelar

# Cambiar estado
PATCH /api/v1/pedidos/{pedidoId}/estado
{
  "estado": "PAGADO"
}
```

## 🔄 Eventos de Kafka

### **OrderCreatedEvent**
```json
{
  "eventType": "OrderCreated",
  "pedidoId": 123,
  "clienteId": 456,
  "items": [...],
  "total": 89.99,
  "estado": "CREADO",
  "estrategiaPrecio": "PROMO_PERRO",
  "timestamp": "2025-08-29T22:00:00"
}
```

### **CartItemAddedEvent**
```json
{
  "eventType": "CartItemAdded",
  "clienteId": 456,
  "productoId": 789,
  "cantidad": 2,
  "cantidadAnterior": 0,
  "timestamp": "2025-08-29T22:00:00"
}
```

## 🎯 Reglas de Negocio Implementadas

### **Carrito**
- ❌ No permite cantidades negativas
- ✅ Valida existencia del producto antes de agregar
- ✅ Valida que el producto esté activo
- ✅ Si el producto ya existe, suma las cantidades
- ✅ Si cantidad = 0, elimina el item automáticamente

### **Checkout**
- ❌ No permite checkout de carrito vacío
- ✅ Valida stock disponible antes de reservar
- ✅ Reserva stock automáticamente (transaccional)
- ✅ Aplica estrategia de pricing según cliente
- ✅ Limpia carrito después de crear pedido
- ✅ Publica evento para sistemas externos

### **Pedidos**
- ✅ Estados válidos: CREADO → PAGADO → EN_PREPARACION → ENVIADO → ENTREGADO
- ✅ Solo se puede cancelar en estados CREADO o PAGADO
- ✅ Al cancelar, libera el stock reservado
- ✅ Transiciones de estado validadas

## 🧪 Cobertura de Tests

Los tests del catálogo existentes siguen funcionando. Para el nuevo caso de uso:

```bash
# Ejecutar tests del catálogo (incluye validaciones de productos)
./run-catalog-tests.sh
```

## 🚀 Ejemplo de Uso Completo

```bash
# 1. Agregar productos al carrito
curl -X POST /api/v1/carritos/cliente/1/items \
  -H "Content-Type: application/json" \
  -d '{"productoId": 1, "cantidad": 2}'

curl -X POST /api/v1/carritos/cliente/1/items \
  -H "Content-Type: application/json" \
  -d '{"productoId": 2, "cantidad": 1}'

# 2. Ver carrito
curl /api/v1/carritos/cliente/1

# 3. Realizar checkout
curl -X POST /api/v1/pedidos/checkout/cliente/1

# 4. Ver pedido creado
curl /api/v1/pedidos/{pedidoId}

# 5. Cambiar estado del pedido
curl -X PATCH /api/v1/pedidos/{pedidoId}/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "PAGADO"}'
```

## 🎉 Resultado

**Caso de uso completo implementado con:**
- ✅ **Arquitectura SOLID** perfecta
- ✅ **Strategy Pattern** para pricing extensible  
- ✅ **Gestión de stock** transaccional
- ✅ **Eventos de Kafka** para integración
- ✅ **API REST** completa y documentada
- ✅ **Validaciones de negocio** robustas
