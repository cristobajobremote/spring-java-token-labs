# 📮 Postman Collection - Spring Java Token Labs API

Esta carpeta contiene la colección completa de Postman para probar todos los endpoints de la API Spring Java Token Labs.

## 📁 Archivos incluidos

- **`Spring-Java-Token-Labs-API.postman_collection.json`** - Colección principal con todos los endpoints
- **`External-Service-Failure-Tests.postman_collection.json`** - Colección específica para pruebas de fallo del servicio externo
- **`Spring-Java-Token-Labs-Environment.postman_environment.json`** - Variables de entorno
- **`README.md`** - Este archivo de documentación

## 🚀 Cómo importar en Postman

### Opción 1: Importar desde archivos
1. Abre Postman
2. Haz clic en **Import** (botón superior izquierdo)
3. Selecciona **Files** y navega a esta carpeta
4. Selecciona ambos archivos `.json`
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
| `baseUrl` | `http://localhost:8080/api/v1` | URL base de la API |
| `swaggerUrl` | `http://localhost:8080/swagger-ui.html` | URL de Swagger UI |
| `actuatorUrl` | `http://localhost:8080/actuator` | URL de Spring Boot Actuator |
| `postgresPort` | `25432` | Puerto de PostgreSQL |
| `redisPort` | `26379` | Puerto de Redis |
| `apiVersion` | `v1` | Versión de la API |

## 📋 Endpoints incluidos

### 🔍 Health & Status
- **GET** `/health` - Health check de la API
- **GET** `/actuator/health` - Health check del sistema (Spring Boot Actuator)

### 🧮 Calculation Endpoints
- **POST** `/calculate` - Realizar cálculo con porcentaje externo
- **GET** `/history` - Obtener historial completo de cálculos
- **GET** `/history/stats` - Obtener estadísticas de cálculos
- **GET** `/history/range` - Obtener historial por rango de fechas

### 🧪 External Service Failure Tests
- **GET** `/test/failure-status` - Verificar estado de simulación de fallo
- **POST** `/test/simulate-failure` - Activar simulación de fallo del servicio externo
- **POST** `/test/disable-failure` - Desactivar simulación de fallo
- **POST** `/test/cache-clear` - Limpiar caché de porcentajes


## 🧪 Tests Automáticos

La colección incluye tests automáticos que se ejecutan después de cada request:

- ✅ Verificación de código de estado exitoso (200, 201, 202)
- ✅ Verificación de tiempo de respuesta < 5000ms
- ✅ Verificación de Content-Type JSON

## 📝 Ejemplos de Uso

### 1. Realizar un cálculo
```json
POST {{baseUrl}}/calculate
Content-Type: application/json

{
  "firstNumber": 10.50,
  "secondNumber": 20.25
}
```

### 2. Obtener historial
```bash
GET {{baseUrl}}/history
```

### 3. Verificar salud de la API
```bash
GET {{baseUrl}}/health
```

## 🔄 Flujo de Pruebas Recomendado

### Flujo Básico
1. **Verificar salud**: `GET /health`
2. **Realizar cálculo**: `POST /calculate`
3. **Verificar historial**: `GET /history`
4. **Obtener estadísticas**: `GET /history/stats`

### Flujo de Pruebas de Fallo del Servicio Externo
1. **Verificar estado inicial**: `GET /test/failure-status`
2. **Realizar cálculo normal**: `POST /calculate` (con servicio funcionando)
3. **Activar simulación de fallo**: `POST /test/simulate-failure`
4. **Probar con caché disponible**: `POST /calculate` (debería usar caché)
5. **Limpiar caché**: `POST /test/cache-clear`
6. **Probar sin caché**: `POST /calculate` (debería fallar)
7. **Desactivar simulación**: `POST /test/disable-failure`
8. **Probar recuperación**: `POST /calculate` (debería funcionar)

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

## 📚 Documentación Adicional

- **Swagger UI**: {{swaggerUrl}}
- **README Principal**: `../README.md`
- **Scripts de Inicio**: `../start.sh`

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
