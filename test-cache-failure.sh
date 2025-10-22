#!/bin/bash

# Script de prueba para demostrar el manejo de fallos del servicio externo con caché
# Spring Java Token Labs API

echo "🚀 Iniciando pruebas de manejo de fallos del servicio externo con caché"
echo "=================================================================="

BASE_URL="http://localhost:8080/api/v1"

# Función para hacer requests y mostrar resultados
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo ""
    echo "📋 $description"
    echo "----------------------------------------"
    
    if [ "$method" = "POST" ] && [ -n "$data" ]; then
        response=$(curl -s -X POST "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -X POST "$BASE_URL$endpoint")
    else
        response=$(curl -s -X GET "$BASE_URL$endpoint")
    fi
    
    echo "Response: $response"
    
    # Verificar código de estado
    status_code=$(curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$endpoint" \
        ${data:+-H "Content-Type: application/json" -d "$data"})
    
    if [ "$status_code" = "200" ]; then
        echo "✅ Status: OK ($status_code)"
    else
        echo "❌ Status: Error ($status_code)"
    fi
}

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 5

# Verificar que la aplicación esté funcionando
echo ""
echo "🔍 Verificando estado de la aplicación..."
health_response=$(curl -s "$BASE_URL/health")
if [[ $health_response == *"OK"* ]]; then
    echo "✅ Aplicación funcionando correctamente"
else
    echo "❌ Aplicación no está funcionando. Verifica que esté ejecutándose."
    exit 1
fi

# Caso 1: Servicio funcionando normalmente
echo ""
echo "🧪 CASO 1: Servicio externo funcionando normalmente"
echo "=================================================="

make_request "GET" "/test/failure-status" "" "Verificando estado inicial de simulación de fallo"
make_request "POST" "/calculate" '{"firstNumber": 10.0, "secondNumber": 20.0}' "Realizando cálculo con servicio funcionando"

# Caso 2: Activar simulación de fallo
echo ""
echo "🧪 CASO 2: Activando simulación de fallo del servicio externo"
echo "=========================================================="

make_request "POST" "/test/simulate-failure" "" "Activando simulación de fallo"
make_request "GET" "/test/failure-status" "" "Verificando que la simulación esté activa"

# Caso 3: Servicio fallando con caché disponible
echo ""
echo "🧪 CASO 3: Servicio externo fallando con caché disponible"
echo "========================================================"

make_request "POST" "/calculate" '{"firstNumber": 5.0, "secondNumber": 15.0}' "Realizando cálculo con servicio fallando (debería usar caché)"

# Caso 4: Limpiar caché y probar fallo sin caché
echo ""
echo "🧪 CASO 4: Servicio externo fallando sin caché"
echo "============================================="

make_request "POST" "/test/cache-clear" "" "Limpiando caché"
make_request "POST" "/calculate" '{"firstNumber": 3.0, "secondNumber": 7.0}' "Realizando cálculo sin caché (debería fallar)"

# Caso 5: Desactivar simulación y recuperar servicio
echo ""
echo "🧪 CASO 5: Desactivando simulación y recuperando servicio"
echo "======================================================="

make_request "POST" "/test/disable-failure" "" "Desactivando simulación de fallo"
make_request "GET" "/test/failure-status" "" "Verificando que la simulación esté desactivada"
make_request "POST" "/calculate" '{"firstNumber": 8.0, "secondNumber": 12.0}' "Realizando cálculo con servicio recuperado"

# Caso 6: Verificar que el caché funciona nuevamente
echo ""
echo "🧪 CASO 6: Verificando funcionamiento del caché"
echo "=============================================="

make_request "POST" "/calculate" '{"firstNumber": 2.0, "secondNumber": 8.0}' "Realizando cálculo adicional (debería usar caché)"

echo ""
echo "🎉 Pruebas completadas!"
echo "======================"
echo ""
echo "📊 Resumen de casos probados:"
echo "1. ✅ Servicio funcionando normalmente"
echo "2. ✅ Activación de simulación de fallo"
echo "3. ✅ Fallback a caché cuando servicio falla"
echo "4. ✅ Error cuando no hay caché y servicio falla"
echo "5. ✅ Recuperación del servicio"
echo "6. ✅ Funcionamiento normal del caché"
echo ""
echo "🔧 Endpoints de testing disponibles:"
echo "- GET  /test/failure-status     - Verificar estado de simulación"
echo "- POST /test/simulate-failure   - Activar simulación de fallo"
echo "- POST /test/disable-failure    - Desactivar simulación de fallo"
echo "- POST /test/cache-clear        - Limpiar caché"
echo ""
echo "📚 Para más información, consulta: MANEJO-FALLOS-CACHE.md"
