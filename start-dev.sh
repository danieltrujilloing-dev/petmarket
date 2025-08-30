#!/bin/bash

# Script para iniciar el entorno de desarrollo de PetMarket

echo "🐾 Iniciando PetMarket Development Environment..."

# Verificar si Docker está ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está ejecutándose. Por favor inicia Docker Desktop."
    exit 1
fi

# Verificar si docker-compose está disponible
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose no está instalado."
    exit 1
fi

echo "📦 Iniciando servicios de Docker..."
docker-compose up -d

echo "⏳ Esperando que los servicios estén listos..."

# Esperar a que PostgreSQL esté listo
echo "🐘 Esperando PostgreSQL..."
until docker-compose exec -T postgres pg_isready -U petmarket_user -d petmarket; do
    sleep 2
done
echo "✅ PostgreSQL está listo"

# Esperar a que Redis esté listo
echo "🔴 Esperando Redis..."
until docker-compose exec -T redis redis-cli ping; do
    sleep 2
done
echo "✅ Redis está listo"

# Esperar a que Kafka esté listo
echo "📨 Esperando Kafka..."
until docker-compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; do
    sleep 2
done
echo "✅ Kafka está listo"

echo ""
echo "🎉 ¡Todos los servicios están listos!"
echo ""
echo "📋 URLs útiles:"
echo "   - Spring Boot App: http://localhost:8080"
echo "   - Actuator Health: http://localhost:8080/actuator/health"
echo "   - Kafka UI: http://localhost:8083"
echo "   - Adminer (PostgreSQL): http://localhost:8081"
echo "   - Redis Commander: http://localhost:8082"
echo ""
echo "🚀 Para iniciar la aplicación Spring Boot:"
echo "   ./gradlew bootRun"
echo ""
echo "📊 Para ver el estado de los servicios:"
echo "   docker-compose ps"
echo ""
echo "📋 Para ver logs:"
echo "   docker-compose logs -f"
echo ""
echo "⏹️  Para parar todos los servicios:"
echo "   docker-compose down"
echo ""
