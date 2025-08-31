# 🐾 Caso de Uso: Adopciones (Event-Driven)

## 🎯 Funcionalidades Implementadas

### **1. Endpoint Síncrono para Crear SolicitudAdopción**
- ✅ **Exponer endpoint para crear SolicitudAdopción** (síncrono)
- ✅ **Validar datos** completos según especificación
- ✅ **Estados implementados**: PENDIENTE | EN_VERIFICACION | APROBADA | RECHAZADA | CANCELADA

### **2. Publicación de Evento AdoptionRequested**
- ✅ **Publicar evento AdoptionRequested** (Kafka)
- ✅ **Datos completos** en el evento para procesamiento asíncrono

### **3. Worker Asíncrono y Servicio Externo Simulado**
- ✅ **Un worker asíncrono consume el evento**
- ✅ **Llama a un servicio externo simulado** (adapter HTTP fake/stub) del refugio
- ✅ **Verificación simulada** con lógica de aprobación/rechazo
- ✅ **Publica AdoptionApproved/AdoptionRejected** según resultado

### **4. Actualización de Estado Basada en Eventos**
- ✅ **Actualizar estado de la solicitud** en base al evento
- ✅ **Consumidores dedicados** para cada tipo de evento de resultado

### **5. Notificación Simulada al Cliente**
- ✅ **Notificación simulada** (log estructurado) al cliente
- ✅ **Asíncrono**: cuando cambia el estado
- ✅ **Logging detallado** para seguimiento completo

## 🏗️ Arquitectura Event-Driven

### **Flujo de Eventos (Según Especificación)**
```
1. Cliente → POST /api/v1/adopciones/solicitudes (síncrono)
2. AdopcionApplicationService → Crear solicitud + validar datos
3. KafkaEventPublisher → AdoptionRequestedEvent
4. Worker asíncrono → AdoptionRequestedEventConsumer
5. RefugioServiceAdapter → Servicio externo simulado (HTTP fake/stub)
6. Verificación → AdoptionApproved/AdoptionRejected
7. KafkaEventPublisher → Evento de resultado
8. AdoptionStatusEventConsumer → Actualizar estado de solicitud
9. NotificationService → Notificación simulada al cliente (asíncrono)
```

### **Arquitectura SOLID**

#### **Single Responsibility Principle (SRP)**
- `AdopcionApplicationService`: Solo gestión de solicitudes
- `RefugioServiceAdapter`: Solo comunicación con refugios
- `AdoptionRequestedEventConsumer`: Solo procesamiento de solicitudes
- `AdoptionStatusEventConsumer`: Solo notificaciones

#### **Open/Closed Principle (OCP)**
- `RefugioServicePort`: Abierto para nuevas implementaciones
- `GestionarAdopcionUseCase`: Extensible sin modificar código
- Sistema de eventos extensible para nuevos tipos

#### **Liskov Substitution Principle (LSP)**
- Todas las implementaciones de puertos son intercambiables
- Eventos de dominio siguen el contrato base

#### **Interface Segregation Principle (ISP)**
- `GestionarAdopcionUseCase`: Solo métodos de adopción
- `RefugioServicePort`: Solo métodos de refugio
- `SolicitudAdopcionRepositoryPort`: Solo persistencia

#### **Dependency Inversion Principle (DIP)**
- Servicios dependen de abstracciones (puertos)
- Inversión de control completa con Spring

## 📡 API REST Endpoints

### **Solicitudes de Adopción**
```bash
# Crear solicitud
POST /api/v1/adopciones/solicitudes
{
  "clienteId": 1,
  "nombreSolicitante": "Juan Pérez",
  "emailSolicitante": "juan@email.com",
  "telefonoSolicitante": "+57 300 123 4567",
  "tipoMascotaDeseada": "PERRO",
  "motivoAdopcion": "Quiero darle amor y cuidado a una mascota",
  "experienciaPrevia": "He tenido perros durante 10 años",
  "situacionVivienda": "Casa propia con jardín amplio"
}

# Obtener solicitud por ID
GET /api/v1/adopciones/solicitudes/{id}

# Listar solicitudes pendientes
GET /api/v1/adopciones/solicitudes/pendientes

# Listar solicitudes por cliente
GET /api/v1/adopciones/solicitudes/cliente/{clienteId}

# Cancelar solicitud
PUT /api/v1/adopciones/solicitudes/{id}/cancelar

# Procesar respuesta manual del refugio (para testing)
PUT /api/v1/adopciones/solicitudes/{id}/procesar-respuesta
{
  "aprobada": true,
  "observaciones": "Solicitud aprobada tras verificación exitosa"
}
```

## 🔄 Eventos de Kafka

### **AdoptionRequestedEvent**
```json
{
  "eventId": "uuid-123",
  "occurredOn": "2025-08-30T20:00:00Z",
  "solicitudId": 1,
  "clienteId": 123,
  "nombreSolicitante": "Juan Pérez",
  "emailSolicitante": "juan@email.com",
  "tipoMascotaDeseada": "PERRO",
  "refugioAsignado": "Refugio Esperanza"
}
```

### **AdoptionApprovedEvent**
```json
{
  "eventId": "uuid-456",
  "occurredOn": "2025-08-30T20:02:00Z",
  "solicitudId": 1,
  "clienteId": 123,
  "nombreSolicitante": "Juan Pérez",
  "emailSolicitante": "juan@email.com",
  "refugioAsignado": "Refugio Esperanza",
  "observacionesRefugio": "Solicitud aprobada tras verificación exitosa"
}
```

### **AdoptionRejectedEvent**
```json
{
  "eventId": "uuid-789",
  "occurredOn": "2025-08-30T20:02:00Z",
  "solicitudId": 2,
  "clienteId": 456,
  "nombreSolicitante": "María García",
  "emailSolicitante": "maria@email.com",
  "refugioAsignado": "Patitas Felices",
  "motivoRechazo": "Solicitud rechazada debido a criterios internos del refugio"
}
```

## 🎯 Reglas de Negocio Implementadas

### **Solicitudes de Adopción**
- ❌ No permite múltiples solicitudes activas por cliente
- ✅ Valida datos obligatorios (nombre, email, teléfono, tipo mascota)
- ✅ Asigna refugio automáticamente al crear solicitud
- ✅ Cambia estado a EN_VERIFICACION al asignar refugio
- ✅ Solo permite cancelar solicitudes en estados válidos

### **Procesamiento Asíncrono**
- ✅ **Simulación de tiempo de procesamiento**: 0.5-2 segundos aleatorio
- ✅ **Decisión aleatoria del refugio**: 80% aprobación, 20% rechazo
- ✅ **Actualización automática de estado** basada en decisión
- ✅ **Publicación de eventos de resultado** (approved/rejected)
- ✅ **Manejo de errores** con logging detallado

### **Estados de Solicitud**
- ✅ **PENDIENTE** → Estado inicial al crear
- ✅ **EN_VERIFICACION** → Al asignar refugio
- ✅ **APROBADA** → Refugio aprueba la solicitud
- ✅ **RECHAZADA** → Refugio rechaza la solicitud
- ✅ **CANCELADA** → Cliente cancela la solicitud

## 🗃️ Base de Datos

### **Tabla `solicitudes_adopcion`**
```sql
CREATE TABLE solicitudes_adopcion (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    nombre_solicitante VARCHAR(255) NOT NULL,
    email_solicitante VARCHAR(255) NOT NULL,
    telefono_solicitante VARCHAR(20) NOT NULL,
    tipo_mascota_deseada VARCHAR(20) NOT NULL CHECK (tipo_mascota_deseada IN ('PERRO', 'GATO', 'AVE', 'CONEJO', 'HAMSTER', 'OTRO')),
    motivo_adopcion TEXT NOT NULL,
    experiencia_previa TEXT,
    situacion_vivienda TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'EN_VERIFICACION', 'APROBADA', 'RECHAZADA', 'CANCELADA')),
    refugio_asignado VARCHAR(255),
    observaciones_refugio TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### **Índices para Performance**
```sql
-- Índices simples
CREATE INDEX idx_solicitud_cliente_id ON solicitudes_adopcion(cliente_id);
CREATE INDEX idx_solicitud_estado ON solicitudes_adopcion(estado);
CREATE INDEX idx_solicitud_tipo_mascota ON solicitudes_adopcion(tipo_mascota_deseada);
CREATE INDEX idx_solicitud_refugio ON solicitudes_adopcion(refugio_asignado);

-- Índices compuestos
CREATE INDEX idx_solicitud_cliente_estado ON solicitudes_adopcion(cliente_id, estado);
CREATE INDEX idx_solicitud_estado_fecha ON solicitudes_adopcion(estado, fecha_creacion);

-- Índice único para evitar solicitudes duplicadas activas
CREATE UNIQUE INDEX idx_solicitud_cliente_activa ON solicitudes_adopcion(cliente_id) 
WHERE estado IN ('PENDIENTE', 'EN_VERIFICACION');
```

## ⚙️ Configuración de Kafka

### **Topics**
```properties
# Configuración de topics en application.properties
kafka.topics.adoption-requested=petmarket.adoptionrequestedevent
kafka.topics.adoption-approved=petmarket.adoptionapprovedevent
kafka.topics.adoption-rejected=petmarket.adoptionrejectedevent

# Consumer Groups
kafka.consumer-groups.adoption-requested=adoption-verification-group
kafka.consumer-groups.adoption-status=adoption-notification-group
```

### **Configuración de Consumidores**
```java
@KafkaListener(
    topics = "${kafka.topics.adoption-requested}", 
    groupId = "${kafka.consumer-groups.adoption-requested}"
)
public void listen(String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
    // Procesamiento asíncrono con acknowledgment manual
}
```

## 🧪 Testing Completo

### **Tests Unitarios Implementados**
```bash
# Ejecutar tests del módulo de adopciones
./gradlew test --tests "*Adopcion*"
```

**Tests implementados:**
- ✅ `AdopcionApplicationServiceTest` - 15 tests de servicio de aplicación
- ✅ `SolicitudAdopcionTest` - 12 tests de entidad de dominio
- ✅ `AdoptionDomainEventTest` - 9 tests de eventos de dominio
- ✅ `RefugioServiceAdapterTest` - 8 tests de adaptador de refugio

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

### **2. Crear Solicitud de Adopción**
```bash
curl -X POST "http://localhost:8080/api/v1/adopciones/solicitudes" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "nombreSolicitante": "Juan Pérez",
    "emailSolicitante": "juan@email.com",
    "telefonoSolicitante": "+57 300 123 4567",
    "tipoMascotaDeseada": "PERRO",
    "motivoAdopcion": "Quiero darle amor y cuidado a una mascota",
    "experienciaPrevia": "He tenido perros durante 10 años",
    "situacionVivienda": "Casa propia con jardín amplio"
  }'
```

### **3. Monitorear Procesamiento Asíncrono**
```bash
# Ver logs de la aplicación para seguir el flujo
tail -f logs/petmarket.log | grep -i adoption

# Verificar estado de la solicitud
curl "http://localhost:8080/api/v1/adopciones/solicitudes/1"
```

### **4. Probar Diferentes Escenarios**
```bash
# Listar solicitudes pendientes
curl "http://localhost:8080/api/v1/adopciones/solicitudes/pendientes"

# Listar solicitudes por cliente
curl "http://localhost:8080/api/v1/adopciones/solicitudes/cliente/1"

# Cancelar solicitud
curl -X PUT "http://localhost:8080/api/v1/adopciones/solicitudes/1/cancelar"
```

### **5. Monitorear Kafka**
```bash
# Ver topics de Kafka
docker exec petmarket-kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Consumir eventos (para debugging)
docker exec petmarket-kafka kafka-console-consumer.sh \
  --topic petmarket.adoptionrequestedevent \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

## 📊 Métricas y Monitoreo

### **Logs Estructurados**
- ✅ **Creación de solicitudes**: Con emojis y IDs para seguimiento
- ✅ **Procesamiento asíncrono**: Tiempo de procesamiento y decisiones
- ✅ **Eventos publicados**: Confirmación de publicación exitosa
- ✅ **Notificaciones**: Simulación de notificaciones al cliente

### **Ejemplos de Logs**
```
🐾 Creando solicitud de adopción para cliente: 1
✅ Solicitud de adopción creada con ID: 1 para cliente: 1
🏥 Procesando respuesta del refugio para solicitud: 1 - Resultado: APROBADA
✅ Respuesta del refugio procesada para solicitud: 1
📧 Notificación enviada: Solicitud de adopción APROBADA para Juan Pérez
```

## 🎉 Resumen del Caso de Uso

### ✅ **Completamente Implementado:**

1. **🐾 Gestión completa de adopciones**
   - Crear, consultar, cancelar solicitudes
   - Validaciones de negocio robustas
   - Estados de solicitud bien definidos

2. **⚡ Arquitectura Event-Driven**
   - Eventos de Kafka para comunicación asíncrona
   - Workers asíncronos para procesamiento
   - Desacoplamiento completo de componentes

3. **🏗️ Arquitectura hexagonal y SOLID**
   - Separación clara de responsabilidades
   - Puertos y adaptadores implementados
   - Principios SOLID aplicados correctamente

4. **🎲 Simulación realista**
   - Servicio externo de refugio simulado
   - Lógica de aprobación/rechazo aleatoria
   - Tiempos de procesamiento variables

5. **🧪 Testing completo**
   - 44 tests unitarios implementados
   - Cobertura de todos los componentes
   - Mocking de dependencias externas

6. **📊 API REST completa**
   - Endpoints síncronos para gestión
   - Procesamiento asíncrono transparente
   - Manejo de errores robusto

**¡El caso de uso de adopciones está 100% funcional con arquitectura event-driven y procesamiento asíncrono! 🎯**
