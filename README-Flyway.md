# Flyway Database Migrations - PetMarket

## 📋 Configuración de Flyway

Flyway está configurado para gestionar las migraciones de base de datos de forma versionada y automática.

### Configuración Actual

- **Ubicación de migraciones**: `src/main/resources/db/migration/`
- **Prefijo**: `V` (ejemplo: `V1__Create_tables.sql`)
- **Separador**: `__` (doble guión bajo)
- **Sufijo**: `.sql`
- **Base de datos**: PostgreSQL

## 📁 Estructura de Migraciones

```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql      # Esquema inicial
├── V2__Insert_initial_data.sql        # Datos iniciales
└── V3__Add_new_feature.sql            # Futuras migraciones
```

## 🎯 Convención de Nombres

### Migraciones Versionadas
- **Formato**: `V{version}__{description}.sql`
- **Ejemplos**:
  - `V1__Create_initial_schema.sql`
  - `V2__Insert_initial_data.sql`
  - `V3__Add_user_roles_table.sql`
  - `V4__Alter_pets_add_price_column.sql`

### Migraciones Repetibles
- **Formato**: `R__{description}.sql`
- **Ejemplos**:
  - `R__Create_views.sql`
  - `R__Update_functions.sql`

## 🚀 Ejecución de Migraciones

### Automática
Las migraciones se ejecutan automáticamente al iniciar la aplicación Spring Boot.

### Manual (usando Gradle)
```bash
# Ejecutar migraciones
./gradlew flywayMigrate

# Ver información de migraciones
./gradlew flywayInfo

# Validar migraciones
./gradlew flywayValidate

# Limpiar base de datos (⚠️ Solo en desarrollo)
./gradlew flywayClean
```

## 📊 Esquema Actual

### Tablas Principales

#### `categories`
- Categorías de mascotas (Dogs, Cats, Birds, etc.)
- Incluye nombre, descripción y timestamps

#### `pets`
- Información de mascotas disponibles
- Estados: AVAILABLE, PENDING, SOLD
- Relación con categorías
- Soporte para múltiples fotos y tags

#### `users`
- Usuarios del sistema (clientes y admins)
- Estados: ACTIVE, INACTIVE, SUSPENDED
- Información de contacto y autenticación

#### `orders`
- Pedidos de mascotas
- Estados: PLACED, APPROVED, DELIVERED, CANCELLED
- Relación con pets y users

### Características Implementadas

- ✅ **Claves foráneas** con integridad referencial
- ✅ **Índices** para optimizar consultas
- ✅ **Constraints** para validación de datos
- ✅ **Triggers** para actualizar `updated_at` automáticamente
- ✅ **Datos iniciales** para desarrollo y testing

## 🛠️ Comandos Útiles

### Ver estado de migraciones
```bash
# En la aplicación Spring Boot
curl http://localhost:8080/actuator/flyway
```

### Conectar a PostgreSQL y verificar
```bash
# Conectar a la base de datos
docker-compose exec postgres psql -U petmarket_user -d petmarket

# Ver tablas creadas
\dt

# Ver datos de ejemplo
SELECT * FROM categories;
SELECT * FROM pets LIMIT 5;
SELECT * FROM users LIMIT 5;
```

## 📝 Mejores Prácticas

### 1. Nombres Descriptivos
```sql
-- ✅ Bueno
V5__Add_pet_price_and_discount_columns.sql

-- ❌ Malo
V5__Update_pets.sql
```

### 2. Migraciones Incrementales
```sql
-- ✅ Cada migración debe ser pequeña e independiente
V6__Add_reviews_table.sql
V7__Add_index_on_reviews_pet_id.sql

-- ❌ No hacer todo en una migración gigante
```

### 3. Backwards Compatibility
```sql
-- ✅ Agregar columnas como nullable inicialmente
ALTER TABLE pets ADD COLUMN price DECIMAL(10,2);

-- Luego en otra migración hacer NOT NULL si es necesario
-- V8__Make_pet_price_not_null.sql
```

### 4. Rollback Strategy
- Flyway no hace rollback automático
- Crear migraciones de rollback manualmente si es necesario
- Ejemplo: `V9__Remove_deprecated_columns.sql`

## 🔧 Configuración Avanzada

### Variables de Entorno para Diferentes Ambientes

```properties
# Development
spring.flyway.baseline-on-migrate=true

# Production
spring.flyway.baseline-on-migrate=false
spring.flyway.validate-on-migrate=true
spring.flyway.clean-disabled=true
```

### Migraciones Condicionales
```sql
-- Ejemplo de migración condicional
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='pets' AND column_name='price') THEN
        ALTER TABLE pets ADD COLUMN price DECIMAL(10,2);
    END IF;
END $$;
```

## 🚨 Troubleshooting

### Error: "Found non-empty schema"
- Usar `spring.flyway.baseline-on-migrate=true`
- O limpiar la base de datos con `docker-compose down -v`

### Error: "Migration checksum mismatch"
- No modificar migraciones ya ejecutadas
- Crear nueva migración para cambios

### Ver logs de Flyway
```properties
logging.level.org.flywaydb=DEBUG
```

## 🎯 Próximos Pasos

1. **Agregar más tablas** según necesidades del negocio
2. **Crear índices adicionales** para optimización
3. **Implementar vistas** para reportes
4. **Agregar stored procedures** si es necesario
5. **Configurar migraciones por ambiente** (dev/staging/prod)
