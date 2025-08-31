# 🔧 Scripts Personales para Monitoreo de Kafka

## 📋 Comandos para Monitorear Eventos de Kafka

### 🚀 **Scripts de Monitoreo Rápido**

#### 1. **Ver todos los topics disponibles**
```bash
#!/bin/bash
# Listar todos los topics de Kafka
docker exec petmarket-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

#### 2. **Monitorear eventos de Adopciones en tiempo real**
```bash
#!/bin/bash
# Script: monitor_adoption_events.sh
echo "🐾 Monitoreando eventos de adopciones..."
echo "Presiona Ctrl+C para salir"
echo "=================================="

# AdoptionRequested Events
echo "📝 AdoptionRequested Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.adoptionrequestedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos AdoptionRequested"

echo ""
echo "✅ AdoptionApproved Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.adoptionapprovedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos AdoptionApproved"   

echo ""
echo "❌ AdoptionRejected Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.adoptionrejectedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos AdoptionRejected"
```

#### 3. **Monitorear eventos de Inventario**
```bash
#!/bin/bash
# Script: monitor_inventory_events.sh
echo "📦 Monitoreando eventos de inventario..."
echo "Presiona Ctrl+C para salir"
echo "=================================="

echo "⚠️  LowStock Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.lowstockevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos LowStock"

echo ""
echo "✅ StockConfirmed Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.stockconfirmedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos StockConfirmed"
```

#### 4. **Monitorear eventos de Carrito y Pedidos**
```bash
#!/bin/bash
# Script: monitor_order_events.sh
echo "🛒 Monitoreando eventos de pedidos..."
echo "Presiona Ctrl+C para salir"
echo "=================================="

echo "🛍️  CartItemAdded Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.cartitemaddedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos CartItemAdded"

echo ""
echo "📋 OrderCreated Events:"
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.ordercreatedevent \
  --from-beginning \
  --timeout-ms 5000 2>/dev/null || echo "No hay eventos OrderCreated"
```

#### 5. **Script completo para monitorear TODOS los eventos**
```bash
#!/bin/bash
# Script: monitor_all_events.sh
echo "🔍 MONITOREANDO TODOS LOS EVENTOS DE PETMARKET"
echo "=============================================="
echo "Timestamp: $(date)"
echo ""

# Función para mostrar eventos de un topic
show_topic_events() {
    local topic=$1
    local description=$2
    local emoji=$3
    
    echo "$emoji $description:"
    echo "Topic: $topic"
    
    # Contar mensajes
    local count=$(docker exec petmarket-kafka kafka-run-class kafka.tools.GetOffsetShell \
        --broker-list localhost:9092 \
        --topic $topic 2>/dev/null | awk -F: '{sum += $3} END {print sum}')
    
    if [ "$count" -gt 0 ]; then
        echo "📊 Total de eventos: $count"
        echo "📄 Últimos eventos:"
        docker exec petmarket-kafka kafka-console-consumer \
            --bootstrap-server localhost:9092 \
            --topic $topic \
            --from-beginning \
            --max-messages 3 \
            --timeout-ms 3000 2>/dev/null | jq . 2>/dev/null || echo "Eventos en formato no JSON"
    else
        echo "📭 No hay eventos en este topic"
    fi
    echo "----------------------------------------"
    echo ""
}

# Monitorear todos los topics
show_topic_events "petmarket.adoptionrequestedevent" "Solicitudes de Adopción" "🐾"
show_topic_events "petmarket.adoptionapprovedevent" "Adopciones Aprobadas" "✅"
show_topic_events "petmarket.adoptionrejectedevent" "Adopciones Rechazadas" "❌"
show_topic_events "petmarket.lowstockevent" "Stock Bajo" "⚠️"
show_topic_events "petmarket.stockconfirmedevent" "Stock Confirmado" "📦"
show_topic_events "petmarket.cartitemaddedevent" "Items Agregados al Carrito" "🛍️"
show_topic_events "petmarket.ordercreatedevent" "Pedidos Creados" "📋"

echo "🏁 Monitoreo completado!"
```

#### 6. **Monitorear un topic específico en tiempo real**
```bash
#!/bin/bash
# Script: monitor_topic_realtime.sh
# Uso: ./monitor_topic_realtime.sh <topic_name>

if [ $# -eq 0 ]; then
    echo "❌ Error: Debes proporcionar el nombre del topic"
    echo "📖 Uso: $0 <topic_name>"
    echo ""
    echo "📋 Topics disponibles:"
    docker exec petmarket-kafka kafka-topics --bootstrap-server localhost:9092 --list
    exit 1
fi

TOPIC=$1
echo "🔴 MONITOREO EN TIEMPO REAL - Topic: $TOPIC"
echo "Presiona Ctrl+C para salir"
echo "================================================"

docker exec -it petmarket-kafka kafka-console-consumer \
    --bootstrap-server localhost:9092 \
    --topic $TOPIC \
    --from-beginning \
    --property print.timestamp=true \
    --property print.key=true \
    --property print.value=true
```

#### 7. **Limpiar todos los eventos de un topic (CUIDADO)**
```bash
#!/bin/bash
# Script: clear_topic_events.sh
# CUIDADO: Este script elimina TODOS los eventos de un topic

if [ $# -eq 0 ]; then
    echo "❌ Error: Debes proporcionar el nombre del topic"
    echo "📖 Uso: $0 <topic_name>"
    exit 1
fi

TOPIC=$1
echo "⚠️  ADVERTENCIA: Vas a eliminar TODOS los eventos del topic: $TOPIC"
echo "¿Estás seguro? (escribe 'SI' para confirmar):"
read confirmation

if [ "$confirmation" = "SI" ]; then
    echo "🗑️  Eliminando eventos del topic $TOPIC..."
    docker exec petmarket-kafka kafka-topics \
        --bootstrap-server localhost:9092 \
        --delete \
        --topic $TOPIC
    
    echo "✅ Topic eliminado. Recreando..."
    docker exec petmarket-kafka kafka-topics \
        --bootstrap-server localhost:9092 \
        --create \
        --topic $TOPIC \
        --partitions 3 \
        --replication-factor 1
    
    echo "✅ Topic $TOPIC recreado y limpio"
else
    echo "❌ Operación cancelada"
fi
```

#### 8. **Estadísticas de todos los topics**
```bash
#!/bin/bash
# Script: kafka_stats.sh
echo "📊 ESTADÍSTICAS DE KAFKA - PETMARKET"
echo "===================================="
echo "Timestamp: $(date)"
echo ""

echo "📋 Topics disponibles:"
docker exec petmarket-kafka kafka-topics --bootstrap-server localhost:9092 --list

echo ""
echo "📊 Información detallada de topics:"
docker exec petmarket-kafka kafka-topics \
    --bootstrap-server localhost:9092 \
    --describe

echo ""
echo "👥 Consumer Groups activos:"
docker exec petmarket-kafka kafka-consumer-groups \
    --bootstrap-server localhost:9092 \
    --list

echo ""
echo "📈 Estado de Consumer Groups:"
docker exec petmarket-kafka kafka-consumer-groups \
    --bootstrap-server localhost:9092 \
    --describe \
    --all-groups
```

---

## 🚀 **Cómo usar estos scripts**

### **Opción 1: Copiar y pegar directamente**
```bash
# Ejemplo: Ver eventos de adopciones
docker exec petmarket-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.adoptionrequestedevent \
  --from-beginning \
  --timeout-ms 5000
```

### **Opción 2: Crear archivos ejecutables**
```bash
# Crear el archivo
nano monitor_adoption_events.sh

# Copiar el contenido del script
# Dar permisos de ejecución
chmod +x monitor_adoption_events.sh

# Ejecutar
./monitor_adoption_events.sh
```

### **Opción 3: Funciones en .bashrc/.zshrc**
```bash
# Agregar al final de ~/.zshrc o ~/.bashrc
alias kafka-topics-list='docker exec petmarket-kafka kafka-topics --bootstrap-server localhost:9092 --list'
alias kafka-adoption-events='docker exec petmarket-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic petmarket.adoptionrequestedevent --from-beginning --timeout-ms 5000'
alias kafka-inventory-events='docker exec petmarket-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic petmarket.lowstockevent --from-beginning --timeout-ms 5000'

# Recargar configuración
source ~/.zshrc
```

---

## 🔧 **Comandos Útiles Adicionales**

### **Ver información de un consumer group específico**
```bash
docker exec petmarket-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group adoption-verification-group
```

### **Ver el lag de un consumer group**
```bash
docker exec petmarket-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group adoption-verification-group \
  --verbose
```

### **Producir un evento manualmente (para testing)**
```bash
# Enviar un evento de prueba
docker exec -it petmarket-kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic petmarket.adoptionrequestedevent
```

### **Ver configuración de un topic**
```bash
docker exec petmarket-kafka kafka-configs \
  --bootstrap-server localhost:9092 \
  --describe \
  --entity-type topics \
  --entity-name petmarket.adoptionrequestedevent
```

---

## 📝 **Notas Importantes**

1. **Prerequisitos**: Docker debe estar corriendo con `docker-compose up -d`
2. **Timeout**: Los scripts usan timeout para evitar esperas infinitas
3. **Formato JSON**: Algunos eventos pueden no estar en formato JSON válido
4. **Permisos**: Los scripts de eliminación requieren confirmación explícita
5. **Monitoreo en tiempo real**: Usa Ctrl+C para salir de los monitores en tiempo real

---

## 🎯 **Topics Principales de PetMarket**

- `petmarket.adoptionrequestedevent` - Solicitudes de adopción
- `petmarket.adoptionapprovedevent` - Adopciones aprobadas  
- `petmarket.adoptionrejectedevent` - Adopciones rechazadas
- `petmarket.lowstockevent` - Alertas de stock bajo
- `petmarket.stockconfirmedevent` - Confirmaciones de stock
- `petmarket.cartitemaddedevent` - Items agregados al carrito
- `petmarket.ordercreatedevent` - Pedidos creados

---

**¡Usa estos scripts para monitorear en tiempo real todos los eventos de tu aplicación PetMarket! 🚀**
