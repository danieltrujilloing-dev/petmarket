# Tests con PostgreSQL - PetMarket

## 📋 Configuración Actual

### 🗄️ Base de Datos para Tests
- **Motor**: PostgreSQL (NO H2)
- **Base de datos**: `petmarket_test`
- **Usuario**: `petmarket_user`
- **Configuración**: Separada de la BD de desarrollo

### 🧪 Tipos de Tests

#### 1. **Tests Unitarios** (Sin BD)
- `ClienteTest.java` - Tests puros de lógica de dominio
- `CarritoTest.java` - Tests puros de lógica de dominio
- **Características**: No requieren base de datos, son rápidos

#### 2. **Tests de Integración** (Con PostgreSQL)
- `PetmarketApplicationTests.java` - Carga del contexto Spring
- `DatabaseIntegrationTest.java` - Integración con PostgreSQL
- **Características**: Usan base de datos real, más lentos pero más realistas

## 🏗️ Arquitectura de Tests

### Clase Base para Integración
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseIntegrationTest {
    // Configuración común para tests de integración
}
```

### Limpieza Automática
- **Script**: `src/test/resources/sql/clean-database.sql`
- **Función**: Limpia todas las tablas antes de cada test
- **Beneficio**: Tests independientes y reproducibles

## 🔧 Configuración

### application-test.properties
```properties
# PostgreSQL para tests
spring.datasource.url=jdbc:postgresql://localhost:5432/petmarket_test
spring.datasource.username=petmarket_user
spring.datasource.password=petmarket_pass

# Flyway habilitado
spring.flyway.enabled=true
spring.flyway.clean-disabled=false

# JPA con create-drop para tests
spring.jpa.hibernate.ddl-auto=create-drop
```

### build.gradle.kts
```kotlin
dependencies {
    // PostgreSQL (sin H2)
    runtimeOnly("org.postgresql:postgresql")
    
    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
```

## 🚀 Comandos para Ejecutar Tests

### Todos los Tests
```bash
./gradlew test
```

### Tests Específicos
```bash
# Tests unitarios (rápidos)
./gradlew test --tests ClienteTest
./gradlew test --tests CarritoTest

# Tests de integración (con BD)
./gradlew test --tests DatabaseIntegrationTest
./gradlew test --tests PetmarketApplicationTests
```

### Con Limpieza
```bash
./gradlew clean test
```

## 📊 Ventajas de PostgreSQL en Tests

### ✅ Beneficios
1. **Realismo**: Misma BD que producción
2. **Compatibilidad**: No hay diferencias entre desarrollo y test
3. **Funciones PostgreSQL**: Pueden usarse en tests (JSONB, etc.)
4. **Detección temprana**: Errores específicos de PostgreSQL se detectan

### ⚙️ Configuración Robusta
1. **Base de datos separada**: `petmarket_test` no interfiere con desarrollo
2. **Limpieza automática**: Cada test empieza con BD limpia
3. **Transacciones**: Rollback automático por defecto
4. **Flyway**: Migraciones aplicadas automáticamente

## 🔍 Verificación del Setup

### 1. Verificar BD de Test
```bash
docker exec petmarket-postgres psql -U petmarket_user -d petmarket_test -c "\\dt"
```

### 2. Verificar Tests
```bash
./gradlew test --info
```

### 3. Verificar Limpieza
Los tests `shouldHaveCleanDatabaseBetweenTests()` verifican que la BD esté limpia.

## 📈 Métricas Actuales

- **Tests Unitarios**: 16 tests (Cliente + Carrito)
- **Tests Integración**: 5 tests (Context + Database)
- **Total**: 21 tests
- **BD**: PostgreSQL 15.14
- **Estado**: ✅ Todos pasando

## 🎯 Próximos Pasos

1. **Repositorios JPA**: Tests de persistencia real
2. **Tests de API**: Tests de controladores REST
3. **Tests de Kafka**: Verificar eventos
4. **Tests de Redis**: Verificar cache

---

**Nota**: Ya no hay rastro de H2 en el proyecto. Todo usa PostgreSQL como debe ser en un entorno real. 🚀
