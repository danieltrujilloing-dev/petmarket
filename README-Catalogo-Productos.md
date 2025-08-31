# Catálogo de Productos - PetMarket

## 🎯 **Caso de Uso Implementado: Catálogo**

Se ha implementado completamente el **caso de uso #1: Catálogo** siguiendo la arquitectura hexagonal, con cache de lectura Redis y invalidación automática según especificaciones.

### ✅ **Funcionalidades Implementadas:**

1. **📋 Listar productos con filtros**
   - **Tipo**: alimento/accesorio (según especificación)
   - **Especie**: especie destino (Perro, Gato, Ave, etc.) 
   - **Rango de precios**: mínimo y máximo
   - Búsqueda por texto (nombre y descripción)
   - Solo productos activos

2. **🚀 Cache de lectura con TTL configurable**
   - **TTL configurable** según especificación
   - TTL por defecto: 15 minutos
   - Cache automático en todas las consultas
   - Claves de cache únicas por filtro

3. **🔄 Invalida la cache cuando se crea/actualiza un producto**
   - ✅ **Al crear productos** → Invalida cache completo
   - ✅ **Al actualizar productos** → Invalida cache completo
   - Al cambiar estado (activar/desactivar)
   - Al modificar stock que afecte disponibilidad

## 🏗️ **Arquitectura Hexagonal Implementada**

```
┌─────────────────────────────────────────────────────────────┐
│                        WEB LAYER                            │
├─────────────────────────────────────────────────────────────┤
│  ProductoController (REST API)                             │
│  - GET /api/v1/productos                                   │
│  - GET /api/v1/productos/buscar                           │
│  - GET /api/v1/productos/tipo/{tipo}                      │
│  - POST /api/v1/productos                                 │
│  - PUT /api/v1/productos/{id}                             │
│  └─ ProductoWebMapper, DTOs                               │
├─────────────────────────────────────────────────────────────┤
│                    APPLICATION LAYER                        │
├─────────────────────────────────────────────────────────────┤
│  ProductoApplicationService                                │
│  ├─ ListarProductosUseCase                                │
│  └─ GestionarProductoUseCase                              │
├─────────────────────────────────────────────────────────────┤
│                       DOMAIN LAYER                          │
├─────────────────────────────────────────────────────────────┤
│  Producto (Entity)                                        │
│  ├─ TipoProducto (Enum)                                   │
│  ├─ EspecieAnimal (Enum)                                  │
│  ├─ FiltroProducto (Value Object)                         │
│  └─ InvalidProductDataException                           │
│                                                            │
│  Ports:                                                   │
│  ├─ ListarProductosUseCase (Input Port)                   │
│  ├─ GestionarProductoUseCase (Input Port)                 │
│  ├─ ProductoRepositoryPort (Output Port)                  │
│  └─ ProductoCachePort (Output Port)                       │
├─────────────────────────────────────────────────────────────┤
│                   INFRASTRUCTURE LAYER                      │
├─────────────────────────────────────────────────────────────┤
│  Persistence:                                             │
│  ├─ ProductoRepositoryAdapter                             │
│  ├─ JpaProductoRepository                                 │
│  ├─ ProductoEntity, ProductoMapper                        │
│  └─ PostgreSQL Database                                   │
│                                                            │
│  Cache:                                                   │
│  ├─ ProductoCacheAdapter                                  │
│  ├─ RedisCacheService                                     │
│  └─ Redis                                                 │
└─────────────────────────────────────────────────────────────┘
```

## 📁 **Archivos Creados/Modificados**

### **🔵 Domain Layer**
```
├── domain/model/producto/
│   ├── Producto.java                 ✅ Entidad principal
│   ├── TipoProducto.java            ✅ Enum de tipos
│   ├── EspecieAnimal.java           ✅ Enum de especies
│   └── FiltroProducto.java          ✅ Value object para filtros
├── domain/exceptions/
│   └── InvalidProductDataException.java  ✅ Excepción de negocio
└── domain/ports/
    ├── in/
    │   ├── ListarProductosUseCase.java     ✅ Puerto entrada listado
    │   └── GestionarProductoUseCase.java   ✅ Puerto entrada gestión
    └── out/
        ├── ProductoRepositoryPort.java     ✅ Puerto salida repositorio
        └── ProductoCachePort.java          ✅ Puerto salida cache
```

### **🟢 Application Layer**
```
└── application/services/
    └── ProductoApplicationService.java  ✅ Servicio de aplicación
```

### **🟡 Infrastructure Layer**
```
├── infrastructure/
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── entity/
│   │   │   │   ├── ProductoEntity.java        ✅ Entidad JPA
│   │   │   │   ├── TipoProductoEntity.java    ✅ Enum JPA
│   │   │   │   └── EspecieAnimalEntity.java   ✅ Enum JPA
│   │   │   ├── repository/
│   │   │   │   └── JpaProductoRepository.java ✅ Repositorio JPA
│   │   │   └── mapper/
│   │   │       └── ProductoMapper.java        ✅ Mapper JPA
│   │   └── repository/
│   │       └── ProductoRepositoryAdapter.java ✅ Adaptador repositorio
│   ├── cache/
│   │   └── ProductoCacheAdapter.java          ✅ Adaptador cache
│   └── config/
│       └── ProductoBeanConfiguration.java     ✅ Configuración beans
```

### **🔴 Web Layer**
```
└── web/
    ├── controller/
    │   └── ProductoController.java     ✅ Controlador REST
    ├── dto/
    │   ├── ProductoResponseDto.java    ✅ DTO respuesta
    │   ├── CreateProductoRequestDto.java ✅ DTO creación
    │   └── UpdateProductoRequestDto.java ✅ DTO actualización
    └── mapper/
        └── ProductoWebMapper.java      ✅ Mapper web
```

### **🟣 Database & Config**
```
├── resources/
│   ├── db/migration/
│   │   ├── V3__Create_productos_table.sql   ✅ Schema productos
│   │   └── V4__Insert_productos_data.sql    ✅ Datos de prueba
│   └── application.properties              ✅ Configuración cache TTL
└── test/
    └── domain/model/producto/
        └── ProductoTest.java                ✅ Tests unitarios JUnit 5
```

## 🌟 **Endpoints de la API REST**

### **📋 Catálogo (Con Cache)**

#### **Listar Todos los Productos Activos**
```http
GET /api/v1/productos
```
- ✅ Cache: 15 minutos
- ✅ Solo productos activos
- ✅ Ordenados por fecha de creación

#### **Búsqueda Avanzada con Filtros**
```http
GET /api/v1/productos/buscar?tipos=Alimento,Accesorio&especies=Perro&precioMin=10.00&precioMax=100.00&q=collar
```
- ✅ Cache: 15 minutos por combinación de filtros
- ✅ Filtros: tipos, especies, precios, texto
- ✅ Clave de cache única por filtro

#### **Productos por Tipo**
```http
GET /api/v1/productos/tipo/Alimento
```
- ✅ Cache: 15 minutos por tipo
- ✅ Tipos válidos: Alimento, Accesorio, Juguete, Medicina, etc.

#### **Productos por Especie**
```http
GET /api/v1/productos/especie/Perro
```
- ✅ Cache: 15 minutos por especie
- ✅ Especies válidas: Perro, Gato, Ave, Pez, etc.

### **🔧 Gestión (Invalida Cache)**

#### **Crear Producto**
```http
POST /api/v1/productos
Content-Type: application/json

{
  "nombre": "Collar Premium para Perro",
  "descripcion": "Collar de cuero genuino con tachas",
  "precio": 45.99,
  "tipo": "Collar",
  "especie": "Perro",
  "stock": 20,
  "activo": true,
  "imagenUrl": "https://example.com/collar.jpg"
}
```
- ✅ **Invalida todo el cache automáticamente**

#### **Actualizar Producto**
```http
PUT /api/v1/productos/{id}
Content-Type: application/json

{
  "nombre": "Collar Premium Actualizado",
  "precio": 49.99,
  "stock": 15
}
```
- ✅ **Invalida todo el cache automáticamente**

#### **Activar/Desactivar Producto**
```http
PATCH /api/v1/productos/{id}/activar
PATCH /api/v1/productos/{id}/desactivar
```
- ✅ **Invalida todo el cache automáticamente**

#### **Actualizar Stock**
```http
PATCH /api/v1/productos/{id}/stock
Content-Type: application/json

{
  "stock": 25
}
```
- ✅ **Invalida cache si afecta disponibilidad**

### **🔧 Utilidades**

#### **Tipos Disponibles**
```http
GET /api/v1/productos/tipos
```

#### **Especies Disponibles**
```http
GET /api/v1/productos/especies
```

#### **Estadísticas de Cache**
```http
GET /api/v1/productos/cache/stats
```

#### **Invalidar Cache Manualmente**
```http
DELETE /api/v1/productos/cache
```

## 🗃️ **Base de Datos**

### **Tabla `productos`**
```sql
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('ALIMENTO', 'ACCESORIO', ...)),
    especie VARCHAR(50) NOT NULL CHECK (especie IN ('PERRO', 'GATO', ...)),
    stock INTEGER CHECK (stock >= 0),
    activo BOOLEAN NOT NULL DEFAULT true,
    imagen_url VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### **Índices para Performance**
```sql
-- Índices simples
CREATE INDEX idx_producto_tipo ON productos(tipo);
CREATE INDEX idx_producto_especie ON productos(especie);
CREATE INDEX idx_producto_activo ON productos(activo);
CREATE INDEX idx_producto_precio ON productos(precio);

-- Índices compuestos
CREATE INDEX idx_producto_tipo_especie ON productos(tipo, especie);
CREATE INDEX idx_producto_activo_disponible ON productos(activo, stock) WHERE stock > 0;

-- Índices de texto completo
CREATE INDEX idx_producto_nombre_text ON productos USING gin(to_tsvector('spanish', nombre));
```

### **Datos de Prueba**
- ✅ **27 productos** de ejemplo
- ✅ **7 especies** diferentes (Perro, Gato, Ave, Pez, etc.)
- ✅ **10 tipos** de productos diferentes
- ✅ Rangos de precios variados
- ✅ Productos con/sin stock
- ✅ Productos activos/inactivos

## 💾 **Sistema de Cache Redis**

### **Configuración**
```properties
# TTL configurable para productos (15 minutos por defecto)
petmarket.cache.productos.ttl=PT15M
```

### **Claves de Cache**
```
petmarket:productos:activos                          # Todos los activos
petmarket:productos:tipo:ALIMENTO                    # Por tipo
petmarket:productos:especie:PERRO                    # Por especie
petmarket:productos:filtro:tipos:ALIMENTO:especies:PERRO:min:10.00:max:50.00  # Filtros complejos
petmarket:producto:123                               # Producto individual
```

### **Estrategia de Invalidación**
- ✅ **Crear producto** → Invalida TODO el cache
- ✅ **Actualizar producto** → Invalida TODO el cache  
- ✅ **Cambiar estado** → Invalida TODO el cache
- ✅ **Actualizar stock** → Invalida cache SI afecta disponibilidad

## 🧪 **Testing con JUnit 5**

### **Tests Unitarios de Dominio**
```bash
./gradlew test --tests "ProductoTest"
```

**18 tests implementados:**
- ✅ Creación de productos válidos
- ✅ Validaciones de datos obligatorios
- ✅ Lógica de negocio (disponibilidad, stock, precios)
- ✅ Filtros y cache keys
- ✅ Activación/desactivación

### **Características JUnit 5 Utilizadas**
- ✅ `@DisplayName` - Nombres descriptivos
- ✅ `@Nested` - Organización jerárquica  
- ✅ `@ParameterizedTest` - Tests parametrizados
- ✅ `@Tag` - Categorización (unit, domain)
- ✅ AssertJ - Assertions fluidas

## 🚀 **Cómo Probar la Implementación**

### **1. Iniciar Servicios**
```bash
# Iniciar PostgreSQL y Redis
docker-compose up -d postgres redis

# Ejecutar migraciones y cargar datos
./gradlew bootRun
```

### **2. Probar Cache de Lectura**
```bash
# Primera consulta (sin cache)
curl "http://localhost:8080/api/v1/productos"

# Segunda consulta (desde cache) - debería ser más rápida
curl "http://localhost:8080/api/v1/productos"

# Verificar estadísticas de cache
curl "http://localhost:8080/api/v1/productos/cache/stats"
```

### **3. Probar Filtros**
```bash
# Por tipo
curl "http://localhost:8080/api/v1/productos/tipo/Alimento"

# Por especie  
curl "http://localhost:8080/api/v1/productos/especie/Perro"

# Filtros complejos
curl "http://localhost:8080/api/v1/productos/buscar?tipos=Alimento&especies=Perro&precioMin=10&precioMax=100"
```

### **4. Probar Invalidación de Cache**
```bash
# Crear producto (invalida cache)
curl -X POST "http://localhost:8080/api/v1/productos" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Producto",
    "precio": 25.99,
    "tipo": "Accesorio", 
    "especie": "Perro",
    "stock": 10
  }'

# Verificar que cache se invalidó
curl "http://localhost:8080/api/v1/productos/cache/stats"
```

### **5. Monitorear Performance**
```bash
# Ver logs de cache hits/misses
tail -f logs/petmarket.log | grep -i cache

# Monitorear Redis
redis-cli monitor
```

## 📊 **Métricas y Performance**

### **Cache Hit Rate Esperado**
- ✅ **Primera consulta**: Miss (va a BD)
- ✅ **Consultas siguientes**: Hit (desde Redis)
- ✅ **TTL**: 15 minutos por defecto
- ✅ **Invalidación**: Automática en cambios

### **Consultas Optimizadas**
- ✅ Índices en columnas de filtro
- ✅ Índices compuestos para consultas comunes
- ✅ Texto completo con GIN index
- ✅ Filtros eficientes en SQL

### **Endpoints de Monitoreo**
```bash
# Health check
curl "http://localhost:8080/actuator/health"

# Métricas
curl "http://localhost:8080/actuator/metrics"
```

## 🎉 **Resumen del Caso de Uso**

### ✅ **Completamente Implementado:**

1. **📋 Catálogo con filtros múltiples**
   - Tipos de producto, especies, precios, texto
   - Paginación implícita y ordenamiento

2. **🚀 Cache de lectura con TTL configurable**
   - Redis como backend de cache
   - 15 minutos TTL por defecto
   - Claves únicas por filtro

3. **🔄 Invalidación automática**
   - Al crear productos
   - Al actualizar productos  
   - Al cambiar estado o stock

4. **🏗️ Arquitectura hexagonal completa**
   - Separación clara de responsabilidades
   - Puertos y adaptadores implementados
   - Inversión de dependencias

5. **🧪 Testing con JUnit 5**
   - Tests unitarios del dominio
   - Características avanzadas de JUnit 5

6. **📊 API REST completa**
   - Endpoints de catálogo
   - Endpoints de gestión
   - Utilidades y monitoreo

**¡El caso de uso del catálogo de productos está 100% funcional con cache Redis y invalidación automática! 🎯**
