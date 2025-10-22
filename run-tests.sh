#!/bin/bash

# 🧪 Script para Ejecutar Tests con Cobertura
# Spring Java Token Labs

echo "🚀 Spring Java Token Labs - Ejecución de Tests con Cobertura"
echo "=============================================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  test          Ejecutar solo los tests"
    echo "  coverage      Ejecutar tests con análisis de cobertura"
    echo "  report        Mostrar reporte de cobertura"
    echo "  clean         Limpiar archivos generados"
    echo "  all           Ejecutar tests + cobertura + reporte (por defecto)"
    echo "  help          Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                    # Ejecutar todo"
    echo "  $0 test              # Solo tests"
    echo "  $0 coverage          # Tests con cobertura"
    echo "  $0 report            # Ver reporte"
}

# Función para limpiar archivos generados
clean_files() {
    echo -e "${YELLOW}🧹 Limpiando archivos generados...${NC}"
    mvn clean
    rm -rf target/site/jacoco/
    rm -rf target/surefire-reports/
    echo -e "${GREEN}✅ Limpieza completada${NC}"
}

# Función para ejecutar solo tests
run_tests() {
    echo -e "${BLUE}🧪 Ejecutando tests...${NC}"
    echo ""
    
    mvn test -Dspring.profiles.active=test
    
    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✅ Tests ejecutados exitosamente${NC}"
    else
        echo ""
        echo -e "${RED}❌ Algunos tests fallaron${NC}"
        return 1
    fi
}

# Función para ejecutar tests con cobertura
run_coverage() {
    echo -e "${BLUE}📊 Ejecutando tests con análisis de cobertura...${NC}"
    echo ""
    
    mvn clean test jacoco:report -Dspring.profiles.active=test
    
    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✅ Análisis de cobertura completado${NC}"
    else
        echo ""
        echo -e "${RED}❌ Error en el análisis de cobertura${NC}"
        return 1
    fi
}

# Función para mostrar reporte de cobertura
show_report() {
    echo -e "${BLUE}📋 Generando reporte de cobertura...${NC}"
    echo ""
    
    if [ -f "target/site/jacoco/index.html" ]; then
        echo -e "${GREEN}✅ Reporte de cobertura generado${NC}"
        echo ""
        echo "📁 Ubicación del reporte:"
        echo "   HTML: target/site/jacoco/index.html"
        echo "   XML:  target/site/jacoco/jacoco.xml"
        echo ""
        
        # Mostrar resumen de cobertura desde el XML
        if [ -f "target/site/jacoco/jacoco.xml" ]; then
            echo -e "${YELLOW}📊 Resumen de Cobertura:${NC}"
            echo "----------------------------------------"
            
            # Extraer métricas del XML usando grep y sed
            INSTRUCTION_COVERED=$(grep -o 'type="INSTRUCTION" missed="[0-9]*" covered="[0-9]*"' target/site/jacoco/jacoco.xml | sed 's/.*missed="\([0-9]*\)" covered="\([0-9]*\)".*/\2/' | head -1)
            INSTRUCTION_MISSED=$(grep -o 'type="INSTRUCTION" missed="[0-9]*" covered="[0-9]*"' target/site/jacoco/jacoco.xml | sed 's/.*missed="\([0-9]*\)" covered="\([0-9]*\)".*/\1/' | head -1)
            
            if [ ! -z "$INSTRUCTION_COVERED" ] && [ ! -z "$INSTRUCTION_MISSED" ]; then
                TOTAL=$((INSTRUCTION_COVERED + INSTRUCTION_MISSED))
                PERCENTAGE=$((INSTRUCTION_COVERED * 100 / TOTAL))
                
                echo "   Instrucciones cubiertas: $INSTRUCTION_COVERED"
                echo "   Instrucciones no cubiertas: $INSTRUCTION_MISSED"
                echo "   Total de instrucciones: $TOTAL"
                echo "   Cobertura: $PERCENTAGE%"
                echo ""
                
                if [ $PERCENTAGE -ge 80 ]; then
                    echo -e "${GREEN}🎉 ¡Excelente cobertura! ($PERCENTAGE%)${NC}"
                elif [ $PERCENTAGE -ge 60 ]; then
                    echo -e "${YELLOW}⚠️  Cobertura aceptable ($PERCENTAGE%)${NC}"
                else
                    echo -e "${RED}❌ Cobertura insuficiente ($PERCENTAGE%)${NC}"
                fi
            fi
        fi
        
        echo ""
        echo "🌐 Para ver el reporte completo en el navegador:"
        echo "   open target/site/jacoco/index.html"
        echo ""
        
    else
        echo -e "${RED}❌ No se encontró el reporte de cobertura${NC}"
        echo "   Ejecuta primero: $0 coverage"
        return 1
    fi
}

# Función para ejecutar todo
run_all() {
    echo -e "${BLUE}🚀 Ejecutando tests completos con cobertura...${NC}"
    echo ""
    
    # Limpiar primero
    clean_files
    echo ""
    
    # Ejecutar tests con cobertura
    run_coverage
    echo ""
    
    # Mostrar reporte
    show_report
}

# Función para verificar prerrequisitos
check_prerequisites() {
    echo -e "${BLUE}🔍 Verificando prerrequisitos...${NC}"
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven no está instalado${NC}"
        exit 1
    fi
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java no está instalado${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Prerrequisitos verificados${NC}"
    echo ""
}

# Función para mostrar estadísticas de tests
show_test_stats() {
    if [ -d "target/surefire-reports" ]; then
        echo -e "${YELLOW}📈 Estadísticas de Tests:${NC}"
        echo "----------------------------------------"
        
        TOTAL_TESTS=$(find target/surefire-reports -name "*.txt" -exec grep -h "Tests run:" {} \; | awk '{sum += $3} END {print sum}')
        FAILED_TESTS=$(find target/surefire-reports -name "*.txt" -exec grep -h "Failures:" {} \; | awk '{sum += $2} END {print sum}')
        ERROR_TESTS=$(find target/surefire-reports -name "*.txt" -exec grep -h "Errors:" {} \; | awk '{sum += $2} END {print sum}')
        SKIPPED_TESTS=$(find target/surefire-reports -name "*.txt" -exec grep -h "Skipped:" {} \; | awk '{sum += $2} END {print sum}')
        
        if [ ! -z "$TOTAL_TESTS" ]; then
            SUCCESS_TESTS=$((TOTAL_TESTS - FAILED_TESTS - ERROR_TESTS - SKIPPED_TESTS))
            
            echo "   Total de tests: $TOTAL_TESTS"
            echo "   Exitosos: $SUCCESS_TESTS"
            echo "   Fallidos: $FAILED_TESTS"
            echo "   Con errores: $ERROR_TESTS"
            echo "   Omitidos: $SKIPPED_TESTS"
            echo ""
        fi
    fi
}

# Procesar argumentos
case "${1:-all}" in
    "test")
        check_prerequisites
        run_tests
        show_test_stats
        ;;
    "coverage")
        check_prerequisites
        run_coverage
        show_test_stats
        ;;
    "report")
        show_report
        ;;
    "clean")
        clean_files
        ;;
    "all")
        check_prerequisites
        run_all
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        echo -e "${RED}❌ Opción no válida: $1${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}🎯 ¡Proceso completado!${NC}"
