#!/bin/bash

echo "🎯 Tests del Caso de Uso: CATÁLOGO DE PRODUCTOS"
echo "=============================================="

# Verificar servicios Docker
if ! docker-compose ps | grep -q "postgres.*Up" || ! docker-compose ps | grep -q "redis.*Up"; then
    echo "🔄 Iniciando servicios Docker..."
    docker-compose up -d
    sleep 10
fi

echo "✅ Servicios Docker activos"
echo ""

# Limpiar y ejecutar tests
./gradlew clean test \
  --tests "ProductoTest" \
  --tests "RedisCacheServiceTest" \
  --tests "RedisUnitTest" \
  --tests "PetmarketApplicationTests"

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 ¡TODOS LOS TESTS DEL CATÁLOGO PASARON!"
    echo ""
    echo "📊 Cobertura del Caso de Uso:"
    echo "✅ Modelo de dominio Producto"
    echo "✅ Cache Redis con TTL"
    echo "✅ Integración completa"
else
    echo ""
    echo "❌ Algunos tests fallaron"
    echo "📋 Ver reporte: build/reports/tests/test/index.html"
fi