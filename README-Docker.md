# PetMarket - Configuración Docker

## Servicios Incluidos

### Servicios Principales
- **PostgreSQL** (puerto 5432) - Base de datos principal
- **Redis** (puerto 6379) - Cache con TTL configurado
- **Kafka** (puerto 9092) - Message broker para eventos
- **Zookeeper** (puerto 2181) - Coordinación para Kafka

### Herramientas de Administración
- **Kafka UI** (puerto 8080) - Interfaz web para monitorear Kafka
- **Adminer** (puerto 8081) - Administrador web para PostgreSQL
- **Redis Commander** (puerto 8082) - Administrador web para Redis

## Comandos Útiles

### Iniciar todos los servicios
```bash
docker-compose up -d
```

### Ver logs de todos los servicios
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico
```bash
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f kafka
```

### Parar todos los servicios
```bash
docker-compose down
```

### Parar y eliminar volúmenes (⚠️ Elimina todos los datos)
```bash
docker-compose down -v
```

### Reiniciar un servicio específico
```bash
docker-compose restart postgres
```

### Ver estado de los servicios
```bash
docker-compose ps
```

## Configuración de la Aplicación

### PostgreSQL
- **Host**: localhost:5432
- **Database**: petmarket
- **Username**: petmarket_user
- **Password**: petmarket_pass

### Redis
- **Host**: localhost:6379
- **TTL por defecto**: 24 horas (86400 segundos)
- **Política de memoria**: allkeys-lru

### Kafka
- **Bootstrap servers**: localhost:9092
- **Topics**: Se crean automáticamente
- **Particiones por defecto**: 3
- **Factor de replicación**: 1

## URLs de Administración

Una vez que los servicios estén ejecutándose:

- **Spring Boot App**: http://localhost:8080
- **Actuator Health**: http://localhost:8080/actuator/health
- **Kafka UI**: http://localhost:8083
- **Adminer (PostgreSQL)**: http://localhost:8081
  - Sistema: PostgreSQL
  - Servidor: postgres
  - Usuario: petmarket_user
  - Contraseña: petmarket_pass
  - Base de datos: petmarket
- **Redis Commander**: http://localhost:8082

## Verificación de Salud

### PostgreSQL
```bash
docker-compose exec postgres pg_isready -U petmarket_user -d petmarket
```

### Redis
```bash
docker-compose exec redis redis-cli ping
```

### Kafka
```bash
docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
```

## Comandos de Desarrollo

### Ejecutar la aplicación Spring Boot
```bash
# Primero inicia los servicios de Docker
docker-compose up -d

# Luego ejecuta la aplicación
./gradlew bootRun
```

### Ejecutar tests
```bash
./gradlew test
```

### Ver endpoints de Actuator
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics

## Estructura de Volúmenes

Los datos persistentes se almacenan en:
- `postgres_data`: Datos de PostgreSQL
- `redis_data`: Datos de Redis (opcional, configurado para persistencia)

## Troubleshooting

### Si Kafka no inicia
```bash
# Reiniciar Zookeeper y Kafka
docker-compose restart zookeeper
sleep 10
docker-compose restart kafka
```

### Si PostgreSQL no acepta conexiones
```bash
# Verificar logs
docker-compose logs postgres

# Reiniciar servicio
docker-compose restart postgres
```

### Limpiar completamente el entorno
```bash
docker-compose down -v
docker-compose up -d
```
