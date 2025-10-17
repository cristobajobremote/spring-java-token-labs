#!/bin/bash

# Script de inicio rápido para Spring Java Token Labs
# Este script facilita el desarrollo y testing de la aplicación

set -e

echo "🚀 Spring Java Token Labs - Script de Inicio Rápido"
echo "=================================================="

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  start-db     - Levantar solo PostgreSQL y Redis"
    echo "  start-all    - Levantar todos los servicios (DB + App)"
    echo "  stop         - Detener todos los servicios"
    echo "  build        - Compilar la aplicación"
    echo "  test         - Ejecutar pruebas unitarias"
    echo "  run          - Ejecutar aplicación localmente"
    echo "  clean        - Limpiar proyecto y contenedores"
    echo "  logs         - Ver logs de los servicios"
    echo "  status       - Ver estado de los servicios"
    echo "  help         - Mostrar esta ayuda"
    echo ""
}

# Función para verificar prerrequisitos
check_prerequisites() {
    echo "🔍 Verificando prerrequisitos..."
    
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker no está instalado"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose no está instalado"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven no está instalado"
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        echo "❌ Java no está instalado"
        exit 1
    fi
    
    echo "✅ Todos los prerrequisitos están instalados"
}

# Función para levantar solo las bases de datos
start_database() {
    echo "🗄️  Levantando PostgreSQL y Redis..."
    docker-compose up -d postgres redis
    
    echo "⏳ Esperando que los servicios estén listos..."
    sleep 10
    
    echo "✅ Bases de datos listas!"
    echo "   PostgreSQL: localhost:5432"
    echo "   Redis: localhost:6379"
}

# Función para levantar todos los servicios
start_all() {
    echo "🏗️  Compilando aplicación..."
    mvn clean package -DskipTests
    
    echo "🐳 Levantando todos los servicios..."
    docker-compose up --build -d
    
    echo "⏳ Esperando que los servicios estén listos..."
    sleep 15
    
    echo "✅ Todos los servicios están ejecutándose!"
    echo "   API: http://localhost:8080"
    echo "   Swagger: http://localhost:8080/swagger-ui.html"
    echo "   PostgreSQL: localhost:5432"
    echo "   Redis: localhost:6379"
}

# Función para detener servicios
stop_services() {
    echo "🛑 Deteniendo servicios..."
    docker-compose down
    echo "✅ Servicios detenidos"
}

# Función para compilar
build_app() {
    echo "🔨 Compilando aplicación..."
    mvn clean compile
    echo "✅ Compilación completada"
}

# Función para ejecutar pruebas
run_tests() {
    echo "🧪 Ejecutando pruebas unitarias..."
    mvn test
    echo "✅ Pruebas completadas"
}

# Función para ejecutar aplicación localmente
run_local() {
    echo "🏃 Ejecutando aplicación localmente..."
    echo "   Asegúrate de que PostgreSQL y Redis estén ejecutándose"
    mvn spring-boot:run
}

# Función para limpiar
clean_all() {
    echo "🧹 Limpiando proyecto..."
    mvn clean
    docker-compose down -v
    docker system prune -f
    echo "✅ Limpieza completada"
}

# Función para ver logs
show_logs() {
    echo "📋 Mostrando logs de los servicios..."
    docker-compose logs -f
}

# Función para ver estado
show_status() {
    echo "📊 Estado de los servicios:"
    docker-compose ps
}

# Función principal
main() {
    check_prerequisites
    
    case "${1:-help}" in
        "start-db")
            start_database
            ;;
        "start-all")
            start_all
            ;;
        "stop")
            stop_services
            ;;
        "build")
            build_app
            ;;
        "test")
            run_tests
            ;;
        "run")
            run_local
            ;;
        "clean")
            clean_all
            ;;
        "logs")
            show_logs
            ;;
        "status")
            show_status
            ;;
        "help"|*)
            show_help
            ;;
    esac
}

# Ejecutar función principal
main "$@"
