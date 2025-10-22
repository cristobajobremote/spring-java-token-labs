# 🧪 Comandos para Ejecutar Tests y Cobertura
# Spring Java Token Labs

## 📋 Comandos Principales

### 1. **Ejecutar Todo (Recomendado)**
```bash
./run-tests.sh
# o
./run-tests.sh all
```
**Descripción:** Ejecuta tests completos con análisis de cobertura y muestra el reporte.

---

### 2. **Solo Tests (Sin Cobertura)**
```bash
./run-tests.sh test
```
**Descripción:** Ejecuta únicamente los tests sin generar reporte de cobertura.

---

### 3. **Tests con Cobertura**
```bash
./run-tests.sh coverage
```
**Descripción:** Ejecuta tests y genera análisis de cobertura.

---

### 4. **Ver Reporte de Cobertura**
```bash
./run-tests.sh report
```
**Descripción:** Muestra el reporte de cobertura (requiere haber ejecutado coverage primero).

---

### 5. **Limpiar Archivos**
```bash
./run-tests.sh clean
```
**Descripción:** Limpia archivos generados (target/, reportes, etc.).

---

## 🔧 Comandos Maven Directos

### **Tests Básicos**
```bash
# Ejecutar tests con perfil de test
mvn test -Dspring.profiles.active=test

# Ejecutar tests específicos
mvn test -Dtest=CalculationControllerTest

# Ejecutar tests de una clase específica
mvn test -Dtest=*ServiceTest
```

### **Tests con Cobertura**
```bash
# Limpiar y ejecutar tests con cobertura
mvn clean test jacoco:report -Dspring.profiles.active=test

# Solo generar reporte de cobertura
mvn jacoco:report

# Verificar cobertura (falla si no alcanza el mínimo)
mvn jacoco:check
```

### **Tests con Debugging**
```bash
# Tests con logging detallado
mvn test -Dspring.profiles.active=test -X

# Tests con información de memoria
mvn test -Dspring.profiles.active=test -Dmaven.test.failure.ignore=true
```

---

## 📊 Comandos de Análisis

### **Ver Reporte HTML**
```bash
# Abrir reporte en navegador (macOS)
open target/site/jacoco/index.html

# Abrir reporte en navegador (Linux)
xdg-open target/site/jacoco/index.html

# Abrir reporte en navegador (Windows)
start target/site/jacoco/index.html
```

### **Análisis de Cobertura por Clase**
```bash
# Ver cobertura de una clase específica
grep -A 10 -B 2 "CalculationController" target/site/jacoco/jacoco.xml

# Ver todas las clases con su cobertura
grep -o 'name="[^"]*"' target/site/jacoco/jacoco.xml | sort | uniq
```

---

## 🚀 Comandos de Desarrollo

### **Tests Rápidos**
```bash
# Ejecutar solo tests que fallaron anteriormente
mvn test -Dspring.profiles.active=test -Dmaven.test.failure.ignore=true

# Ejecutar tests en paralelo
mvn test -Dspring.profiles.active=test -T 4

# Tests con timeout personalizado
mvn test -Dspring.profiles.active=test -Dsurefire.timeout=300
```

### **Tests de Integración**
```bash
# Ejecutar tests de integración
mvn verify -Dspring.profiles.active=test

# Tests con base de datos real (requiere Docker)
mvn test -Dspring.profiles.active=integration-test
```

---

## 📈 Comandos de Monitoreo

### **Ver Estadísticas de Tests**
```bash
# Ver resumen de tests ejecutados
find target/surefire-reports -name "*.txt" -exec cat {} \;

# Contar tests por tipo
find src/test/java -name "*Test.java" -exec grep -l "@Test" {} \; | wc -l

# Ver tests más lentos
grep -r "Time elapsed" target/surefire-reports/ | sort -k3 -nr
```

### **Análisis de Rendimiento**
```bash
# Tests con profiling
mvn test -Dspring.profiles.active=test -Dspring.profiles.include=profiling

# Ver uso de memoria durante tests
mvn test -Dspring.profiles.active=test -Dmaven.test.jvmargs="-Xmx2g -XX:+PrintGCDetails"
```

---

## 🛠️ Comandos de Troubleshooting

### **Problemas Comunes**
```bash
# Limpiar completamente
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/com/tokenlabs/

# Recompilar desde cero
mvn clean compile test-compile

# Verificar dependencias
mvn dependency:tree

# Verificar configuración
mvn help:effective-pom
```

### **Debug de Tests**
```bash
# Tests con debug remoto
mvn test -Dspring.profiles.active=test -Dmaven.test.jvmargs="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Tests con logging específico
mvn test -Dspring.profiles.active=test -Dlogging.level.com.tokenlabs=DEBUG
```

---

## 📋 Checklist de Ejecución

### **Antes de Ejecutar Tests:**
- [ ] Verificar que Docker esté ejecutándose (para tests de integración)
- [ ] Verificar que no haya procesos usando los puertos 8080, 25432, 26379
- [ ] Verificar que Maven esté instalado y configurado
- [ ] Verificar que Java 21 esté instalado

### **Después de Ejecutar Tests:**
- [ ] Revisar el reporte de cobertura en `target/site/jacoco/index.html`
- [ ] Verificar que la cobertura sea >= 80%
- [ ] Revisar logs de tests fallidos en `target/surefire-reports/`
- [ ] Limpiar archivos temporales si es necesario

---

## 🎯 Objetivos de Cobertura

- **Mínimo Aceptable:** 80%
- **Objetivo:** 90%
- **Excelente:** 95%+

### **Por Capa:**
- **Controller:** 100% ✅
- **Service:** 100% ✅
- **Repository:** 100% ✅
- **Configuration:** 100% ✅
- **DTO/Model:** 100% ✅

---

## 📞 Soporte

Si encuentras problemas:

1. **Verificar logs:** `target/surefire-reports/`
2. **Limpiar proyecto:** `./run-tests.sh clean`
3. **Revisar configuración:** `src/test/resources/application-test.yml`
4. **Verificar dependencias:** `mvn dependency:tree`

**¡Happy Testing!** 🧪✨
