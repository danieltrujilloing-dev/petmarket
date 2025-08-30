# Arquitectura Hexagonal - PetMarket (Marketplace)

## 📐 Descripción de la Arquitectura

La **Arquitectura Hexagonal** (también conocida como **Ports and Adapters**) está implementada en este proyecto de **marketplace de productos para mascotas** para mantener la lógica de negocio independiente de los detalles técnicos.

### 🏗️ Principios Fundamentales

1. **Separación de responsabilidades**: Cada capa tiene una responsabilidad específica
2. **Inversión de dependencias**: El dominio no depende de la infraestructura
3. **Testabilidad**: Fácil testing del dominio sin dependencias externas
4. **Flexibilidad**: Fácil cambio de tecnologías sin afectar el negocio

## 📁 Estructura de Paquetes Actual

```
src/main/java/com/interview/petmarket/
├── domain/                     # 🎯 NÚCLEO (sin dependencias externas)
│   ├── model/                  # Entidades y Value Objects del Marketplace
│   │   ├── producto/          # Agregado Producto (alimentos/accesorios)
│   │   ├── cliente/           # Agregado Cliente (usuarios del marketplace)
│   │   ├── carrito/           # Agregado Carrito (compras)
│   │   ├── pedido/            # Agregado Pedido (órdenes completadas)
│   │   ├── inventario/        # Agregado Inventario (stock y reposición)
│   │   ├── solicitud/         # Agregado SolicitudAdopción (adopciones)
│   │   └── common/            # Objetos base comunes (BaseEntity, ValueObject)
│   ├── ports/                 # Contratos (interfaces)
│   │   ├── in/                # Puertos de entrada (Use Cases) - POR IMPLEMENTAR
│   │   └── out/               # Puertos de salida (Repository, Events)
│   ├── exceptions/            # Excepciones específicas del dominio
│   └── events/                # Eventos de dominio (DomainEvent base)
│
├── application/               # 🎯 CASOS DE USO - POR IMPLEMENTAR
│   ├── services/              # Servicios de aplicación
│   └── dto/                   # DTOs internos
│
├── infrastructure/            # 🔧 ADAPTADORES DE SALIDA
│   ├── persistence/           # Persistencia - POR IMPLEMENTAR
│   │   ├── jpa/              # Entidades y repositorios JPA
│   │   └── repository/       # Implementaciones de repositorios
│   ├── messaging/            # Messaging (Kafka implementado)
│   │   └── kafka/            # KafkaEventPublisherAdapter
│   └── config/               # Configuración - POR IMPLEMENTAR
│
└── web/                      # 🌐 ADAPTADORES DE ENTRADA - POR IMPLEMENTAR
    ├── controller/           # Controladores REST
    ├── dto/                  # DTOs de API
    ├── mapper/               # Mappers web ↔ dominio
    └── exception/            # Manejo de excepciones web
```

## 🎯 Capas de la Arquitectura

### 1. **Dominio** (Core) ✅ IMPLEMENTADO
- **Responsabilidad**: Lógica de negocio pura del marketplace
- **Dependencias**: Ninguna (solo Java estándar)
- **Contenido**:
  - **Entidades principales**: `Producto`, `Cliente`, `Carrito`, `Pedido`, `Inventario`, `SolicitudAdopcion`
  - **Value Objects**: `CarritoItem`, `PedidoItem`
  - **Enums**: `TipoProducto`, `EstadoPedido`, `EstadoSolicitud`
  - **Excepciones de dominio**: Específicas por agregado
  - **Eventos de dominio**: Base `DomainEvent`
  - **Puertos de salida**: `EventPublisherPort` (implementado con Kafka)

### 2. **Aplicación** (Use Cases) 🔄 POR IMPLEMENTAR
- **Responsabilidad**: Orquestar casos de uso del marketplace
- **Dependencias**: Solo el dominio
- **Casos de uso futuros**:
  - `CrearProductoUseCase`, `ActualizarInventarioUseCase`
  - `RegistrarClienteUseCase`, `GestionarCarritoUseCase`
  - `ProcesarPedidoUseCase`, `CambiarEstadoPedidoUseCase`
  - `SolicitarAdopcionUseCase`, `ProcesarSolicitudUseCase`

### 3. **Infraestructura** (Adapters) 🔧 PARCIALMENTE IMPLEMENTADO
- **Responsabilidad**: Detalles técnicos del marketplace
- **Dependencias**: Dominio y frameworks (JPA, Kafka, Redis)
- **Implementado**:
  - ✅ `KafkaEventPublisherAdapter` - Publicación de eventos
  - ✅ Migraciones Flyway con esquema completo
- **Por implementar**:
  - 🔄 Repositorios JPA para cada entidad
  - 🔄 Mappers JPA ↔ Dominio
  - 🔄 Cache Redis para consultas frecuentes

### 4. **Web** (Presentation) 🌐 POR IMPLEMENTAR
- **Responsabilidad**: API REST del marketplace
- **Dependencias**: Dominio y Spring Web
- **Endpoints futuros**:
  - 🔄 `/api/productos` - CRUD productos, buscar por tipo/especie
  - 🔄 `/api/clientes` - Registro y gestión de clientes
  - 🔄 `/api/carritos` - Gestión de carritos de compra
  - 🔄 `/api/pedidos` - Procesamiento de órdenes
  - 🔄 `/api/inventario` - Consulta de stock disponible
  - 🔄 `/api/adopciones` - Solicitudes de adopción

## 🔌 Puertos y Adaptadores

### Puertos de Entrada (Driving Ports) - Ejemplos del Marketplace
```java
// Definen QUÉ puede hacer el sistema de marketplace
public interface CrearProductoUseCase {
    Producto crearProducto(CrearProductoCommand command);
}

public interface GestionarCarritoUseCase {
    Carrito agregarProducto(Long clienteId, Long productoId, int cantidad);
    Carrito actualizarCantidad(Long clienteId, Long productoId, int nuevaCantidad);
}

public interface ProcesarPedidoUseCase {
    Pedido crearPedido(Long clienteId);
    Pedido cambiarEstado(Long pedidoId, EstadoPedido nuevoEstado);
}
```

### Puertos de Salida (Driven Ports) - Marketplace
```java
// Definen QUÉ necesita el sistema del exterior
public interface ProductoRepositoryPort {
    Producto save(Producto producto);
    Optional<Producto> findById(Long id);
    List<Producto> findByTipo(TipoProducto tipo);
}

public interface CarritoRepositoryPort {
    Carrito save(Carrito carrito);
    Optional<Carrito> findByClienteId(Long clienteId);
}

// ✅ YA IMPLEMENTADO
public interface EventPublisherPort {
    void publishEvent(DomainEvent event);
}
```

### Adaptadores de Entrada (Futuros)
```java
// Implementan cómo el exterior accede al sistema de marketplace
@RestController
public class ProductoController {
    // Convierte HTTP requests → CrearProductoUseCase
}

@RestController
public class CarritoController {
    // Convierte HTTP requests → GestionarCarritoUseCase
}
```

### Adaptadores de Salida
```java
// Implementan cómo el sistema accede al exterior
@Repository
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {
    // Convierte Use Cases → JPA/Database
}

// ✅ YA IMPLEMENTADO
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    // Publica eventos de dominio a Kafka
}
```

## 🔄 Flujo de Datos del Marketplace

```
HTTP Request → Controller → Use Case → Domain Logic → Repository Port → JPA Adapter → PostgreSQL
     ↑              ↓           ↓            ↓              ↓              ↓
Web Layer    Application   Domain      Infrastructure   Persistence

                                    ↓
                              Event Publisher → Kafka → Microservicios
```

### Ejemplo: Agregar Producto al Carrito

1. **Web**: `CarritoController.agregarProducto()` recibe HTTP POST
2. **Web**: `CarritoWebMapper` convierte DTO → Command
3. **Application**: `GestionarCarritoUseCase.agregarProducto()` ejecuta caso de uso
4. **Domain**: `Carrito.agregarItem()` aplica lógica de negocio y validaciones
5. **Infrastructure**: `CarritoRepositoryAdapter` persiste en PostgreSQL
6. **Infrastructure**: `KafkaEventPublisher` publica `ProductoAgregadoAlCarritoEvent`
7. **Web**: Retorna carrito actualizado como JSON

## 🧪 Ventajas para Testing

### Testing del Dominio del Marketplace
```java
@Test
void shouldCreateProductoWithValidData() {
    // Given
    Producto producto = Producto.builder()
        .withNombre("Purina Pro Plan")
        .withTipo(TipoProducto.ALIMENTO)
        .withEspecieDestino("Perro")
        .withPrecio(new BigDecimal("85000"))
        .build();
    
    // When & Then - Sin dependencias externas
    assertThat(producto.getNombre()).isEqualTo("Purina Pro Plan");
    assertThat(producto.isActivo()).isTrue();
    assertThat(producto.esPara("Perro")).isTrue();
}

@Test
void shouldManageCarritoItemsCorrectly() {
    // Given
    Carrito carrito = Carrito.builder()
        .withClienteId(1L)
        .build();
    
    // When
    carrito.agregarItem(101L, 2);
    carrito.agregarItem(102L, 1);
    
    // Then - Lógica de negocio pura
    assertThat(carrito.contarItems()).isEqualTo(3);
    assertThat(carrito.contieneProducto(101L)).isTrue();
}
```

### Testing de Use Cases (Futuro)
```java
@Test
void shouldProcessOrderSuccessfully() {
    // Given
    ProductoRepositoryPort mockProductoRepo = mock(ProductoRepositoryPort.class);
    CarritoRepositoryPort mockCarritoRepo = mock(CarritoRepositoryPort.class);
    EventPublisherPort mockPublisher = mock(EventPublisherPort.class);
    
    ProcesarPedidoUseCase useCase = new ProcesarPedidoService(
        mockProductoRepo, mockCarritoRepo, mockPublisher);
    
    // When & Then - Con mocks, sin infraestructura real
}
```

## 🔧 Configuración de Dependencias (Futuro)

Spring Boot manejará la inyección de dependencias del marketplace:

```java
@Configuration
public class MarketplaceBeanConfiguration {
    
    @Bean
    public CrearProductoUseCase crearProductoUseCase(
            ProductoRepositoryPort productoRepositoryPort,
            EventPublisherPort eventPublisherPort) {
        return new CrearProductoService(productoRepositoryPort, eventPublisherPort);
    }
    
    @Bean
    public GestionarCarritoUseCase gestionarCarritoUseCase(
            CarritoRepositoryPort carritoRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort,
            EventPublisherPort eventPublisherPort) {
        return new GestionarCarritoService(carritoRepositoryPort, productoRepositoryPort, eventPublisherPort);
    }
    
    // ✅ Ya tenemos implementado el EventPublisher
    @Bean
    public EventPublisherPort eventPublisherPort(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaEventPublisherAdapter(kafkaTemplate);
    }
}
```

## 📋 Reglas de Dependencias

### ✅ Permitido
- Dominio → Java estándar únicamente
- Aplicación → Dominio
- Infraestructura → Dominio + Frameworks
- Web → Dominio + Spring Web

### ❌ Prohibido
- Dominio → Infraestructura
- Dominio → Web
- Aplicación → Infraestructura
- Aplicación → Web

## 🎯 Beneficios del Marketplace Implementados

1. **✅ Mantenibilidad**: Cambios en PostgreSQL/Redis no afectan lógica de negocio
2. **✅ Testabilidad**: Tests unitarios rápidos del dominio sin dependencias
3. **✅ Flexibilidad**: Fácil cambio de PostgreSQL a MongoDB, Kafka a RabbitMQ
4. **✅ Escalabilidad**: Diferentes adaptadores para diferentes contextos
5. **✅ Claridad**: Separación clara entre "qué hace" (dominio) y "cómo lo hace" (infraestructura)
6. **✅ Negocio**: Lógica de marketplace centralizada en entidades de dominio
7. **✅ Eventos**: Sistema de eventos preparado para microservicios

## 🏗️ Estado Actual del Proyecto

### ✅ **Completado**
- **Dominio completo**: 6 entidades principales con lógica de negocio
- **Esquema de BD**: Migraciones Flyway con datos de ejemplo
- **Eventos**: Sistema base para comunicación asíncrona
- **Validaciones**: Reglas de negocio en entidades de dominio
- **Arquitectura limpia**: Sin código obsoleto o dependencias circulares

### 🔄 **Siguientes Pasos**
1. **Puertos de entrada**: Implementar casos de uso
2. **Repositorios JPA**: Adaptadores de persistencia
3. **Controladores REST**: API del marketplace
4. **Cache Redis**: Optimización de consultas frecuentes
5. **Tests**: Cobertura completa del dominio

### 🚀 **Funcionalidades Futuras**
1. **CQRS**: Separar comandos de consultas
2. **Event Sourcing**: Auditoría completa de eventos
3. **Saga Pattern**: Transacciones distribuidas complejas
4. **API Gateway**: Punto único de entrada
5. **Microservicios**: Separación por contextos de negocio

## 📊 **Métricas del Proyecto**
- **21 archivos Java** en el dominio
- **6 entidades principales** del marketplace
- **8 tablas** en PostgreSQL con relaciones
- **0 dependencias circulares** en la arquitectura
- **100% compilación** sin errores
