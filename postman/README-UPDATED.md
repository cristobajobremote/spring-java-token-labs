# 📮 Postman Collection - Spring Java Token Labs API

Esta carpeta contiene las colecciones completas de Postman para probar todos los endpoints de la API Spring Java Token Labs.

## 📁 Archivos incluidos

- **`Spring-Java-Token-Labs-API.postman_collection.json`** - Colección principal completa con todos los endpoints
- **`External-Service-Failure-Tests.postman_collection.json`** - Colección específica para pruebas de fallo del servicio externo
- **`Spring-Java-Token-Labs-Test-Examples.postman_collection.json`** - Colección de ejemplos prácticos de uso
- **`Spring-Java-Token-Labs-Environment.postman_environment.json`** - Variables de entorno
- **`README.md`** - Este archivo de documentación

## 🎯 **Colecciones Disponibles**

### 1. **Spring-Java-Token-Labs-API.postman_collection.json**
**Colección principal completa** con todos los endpoints de la API.

#### **Carpetas incluidas:**
- ✅ **Health Check** - Verificación del estado de la aplicación
- ✅ **Cálculos** - Endpoints principales para realizar cálculos
- ✅ **Historial de Cálculos** - Consulta del historial de cálculos realizados
- ✅ **Gestión de Historial de Requests** - Monitoreo detallado de requests
- ✅ **Testing de Caché** - Endpoints para probar el sistema de caché
- ✅ **External Service Failure Tests** - Testing de fallos del servicio externo
- ✅ **Documentación API** - Acceso a Swagger UI y OpenAPI

### 2. **External-Service-Failure-Tests.postman_collection.json**
**Colección especializada** para probar el manejo de fallos del servicio externo.

#### **Escenarios de testing:**
- ✅ **Escenario 1: Servicio Normal** - Comportamiento estándar
- ✅ **Escenario 2: Fallo con Caché Disponible** - Resilencia con caché
- ✅ **Escenario 3: Fallo sin Caché** - Fallo apropiado sin caché
- ✅ **Escenario 4: Recuperación del Servicio** - Post-recuperación
- ✅ **Escenario 5: Pruebas de Resilencia** - Múltiples escenarios

### 3. **Spring-Java-Token-Labs-Test-Examples.postman_collection.json**
**Colección de ejemplos prácticos** para diferentes casos de uso.

#### **Categorías de ejemplos:**
- ✅ **Ejemplos Básicos** - Cálculos simples y con decimales
- ✅ **Consultas de Historial** - Diferentes formas de consultar historial
- ✅ **Monitoreo de Requests** - Análisis de requests y errores
- ✅ **Testing de Caché** - Verificación del funcionamiento del caché
- ✅ **Testing de Fallos** - Pruebas completas de manejo de fallos

## 🚀 Cómo importar en Postman

### Opción 1: Importar desde archivos
1. Abre Postman
2. Haz clic en **Import** (botón superior izquierdo)
3. Selecciona **Files** y navega a esta carpeta
4. Selecciona los archivos `.json` de las colecciones
5. Haz clic en **Import**

### Opción 2: Importar desde URL
1. Abre Postman
2. Haz clic en **Import**
3. Selecciona **Link** y pega la URL del repositorio
4. Selecciona los archivos de la colección

## 🔧 Configuración de Variables de Entorno

La colección incluye las siguientes variables predefinidas:

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `baseUrl` | `http://localhost:8080` | URL base de la API |

## 📋 Endpoints incluidos

### 🔍 Health & Status
- **GET** `/api/v1/health` - Health check de la API

### 🧮 Calculation Endpoints
- **POST** `/api/v1/calculate` - Realizar cálculo con porcentaje externo
- **GET** `/api/v1/stats` - Estadísticas de cálculos
- **GET** `/api/v1/calculations/history` - Historial de cálculos (paginado)

### 📊 Request History Management
- **GET** `/api/v1/history` - Historial de requests (paginado)
- **GET** `/api/v1/history/stats` - Estadísticas de requests
- **POST** `/api/v1/history/cleanup` - Limpiar registros antiguos

### 🧪 External Service Failure Tests
- **GET** `/api/v1/test/failure-status` - Verificar estado de simulación de fallo
- **POST** `/api/v1/test/simulate-failure` - Activar simulación de fallo del servicio externo
- **POST** `/api/v1/test/disable-failure` - Desactivar simulación de fallo
- **POST** `/api/v1/test/cache-clear` - Limpiar caché de porcentajes

### 📚 Documentation
- **GET** `/swagger-ui/index.html` - Swagger UI
- **GET** `/v3/api-docs` - OpenAPI JSON

## 🧪 Tests Automáticos

La colección incluye tests automáticos que se ejecutan después de cada request:

- ✅ Verificación de código de estado exitoso (200, 201, 202)
- ✅ Verificación de tiempo de respuesta < 5000ms
- ✅ Verificación de Content-Type JSON

## 📝 Ejemplos de Uso

### 1. Realizar un cálculo
```json
POST {{baseUrl}}/api/v1/calculate
Content-Type: application/json

{
  "firstNumber": 10.50,
  "secondNumber": 20.25
}
```

### 2. Obtener historial de cálculos
```bash
GET {{baseUrl}}/api/v1/calculations/history?page=0&size=10
```

### 3. Verificar salud de la API
```bash
GET {{baseUrl}}/api/v1/health
```

### 4. Probar manejo de fallos
```bash
# Activar simulación de fallo
POST {{baseUrl}}/api/v1/test/simulate-failure

# Realizar cálculo (debe usar caché si está disponible)
POST {{baseUrl}}/api/v1/calculate
Content-Type: application/json
{
  "firstNumber": 5,
  "secondNumber": 15
}
```

## 🔄 Flujo de Pruebas Recomendado

### Flujo Básico
1. **Verificar salud**: `GET /api/v1/health`
2. **Realizar cálculo**: `POST /api/v1/calculate`
3. **Verificar historial**: `GET /api/v1/calculations/history`
4. **Obtener estadísticas**: `GET /api/v1/stats`

### Flujo de Pruebas de Fallo del Servicio Externo
1. **Verificar estado inicial**: `GET /api/v1/test/failure-status`
2. **Realizar cálculo normal**: `POST /api/v1/calculate` (con servicio funcionando)
3. **Activar simulación de fallo**: `POST /api/v1/test/simulate-failure`
4. **Probar con caché disponible**: `POST /api/v1/calculate` (debería usar caché)
5. **Limpiar caché**: `POST /api/v1/test/cache-clear`
6. **Probar sin caché**: `POST /api/v1/calculate` (debería fallar con error 500)
7. **Desactivar simulación**: `POST /api/v1/test/disable-failure`
8. **Probar recuperación**: `POST /api/v1/calculate` (debería funcionar)

## 🎯 **Comportamiento de Fallos Corregido**

### ✅ **Comportamiento Correcto Implementado:**
- ✅ **Servicio externo funciona** → Retorna valor del servicio externo
- ✅ **Servicio externo falla + caché disponible** → Retorna valor del caché
- ❌ **Servicio externo falla + caché vacío** → **Lanza RuntimeException (Error 500)**

### 🔧 **Testing de Resilencia:**
1. **Con caché disponible**: El sistema debe funcionar usando el valor del caché
2. **Sin caché disponible**: El sistema debe fallar apropiadamente con error 500
3. **Post-recuperación**: El sistema debe funcionar normalmente

## 🐛 Troubleshooting

### Error de conexión
- Verifica que la aplicación esté ejecutándose en `http://localhost:8080`
- Verifica que PostgreSQL esté ejecutándose en el puerto `25432`
- Verifica que Redis esté ejecutándose en el puerto `26379`

### Error 404
- Verifica que estés usando la variable `{{baseUrl}}` correctamente
- Asegúrate de que la aplicación esté completamente iniciada

### Error 500
- Revisa los logs de la aplicación
- Verifica la conexión a la base de datos
- **Para fallos del servicio externo**: Este es el comportamiento esperado cuando no hay caché disponible

## 📚 Documentación Adicional

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **README Principal**: `../README.md`
- **Scripts de Inicio**: `../start.sh`
- **Corrección de Fallos**: `../CORRECCION-FALLO-SERVICIO-EXTERNO.md`

## 🔧 Personalización

Para cambiar la configuración:

1. **Cambiar URL base**: Modifica la variable `baseUrl` en el entorno
2. **Agregar headers**: Edita la colección para incluir headers personalizados
3. **Modificar tests**: Edita los scripts de test en la colección

## 📞 Soporte

Si encuentras algún problema con la colección:

1. Verifica que todos los servicios estén ejecutándose
2. Revisa la configuración de variables de entorno
3. Consulta los logs de la aplicación
4. Revisa la documentación en `../README.md`
5. Consulta la corrección de fallos en `../CORRECCION-FALLO-SERVICIO-EXTERNO.md`

## 🚀 **Quick Start**

1. **Importar colecciones** en Postman
2. **Configurar environment** con `baseUrl`
3. **Ejecutar "Health Status"** para verificar conectividad
4. **Ejecutar "Realizar Cálculo"** para probar funcionalidad básica
5. **Explorar diferentes escenarios** según necesidades

¡Las colecciones están listas para usar! 🎉
