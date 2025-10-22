# 📊 Guía de Endpoints de Historial de Requests

## 🎯 Descripción General

Los nuevos endpoints de `/history` proporcionan un sistema completo de auditoría y monitoreo de requests con las siguientes funcionalidades:

- **Registro automático** de todos los requests HTTP
- **Paginación completa** para manejar grandes volúmenes de datos
- **Filtros avanzados** para búsquedas específicas
- **Estadísticas detalladas** del historial
- **Limpieza automática** de registros antiguos

## 📋 Endpoints Disponibles

### 1. **GET /history** - Historial Paginado

Obtiene el historial detallado de requests con paginación y filtros opcionales.

#### Parámetros de Paginación:
- `page` (opcional): Número de página (0-based, default: 0)
- `size` (opcional): Tamaño de página (default: 20, máximo: 100)
- `sortBy` (opcional): Campo para ordenar (default: "requestDate")
- `sortDirection` (opcional): Dirección de ordenamiento "asc" o "desc" (default: "desc")

#### Parámetros de Filtros:
- `endpoint` (opcional): Filtrar por endpoint (búsqueda parcial)
- `httpMethod` (opcional): Filtrar por método HTTP (GET, POST, etc.)
- `responseStatus` (opcional): Filtrar por código de respuesta (200, 400, 500, etc.)
- `hasError` (opcional): Filtrar por errores (true/false)
- `startDate` (opcional): Fecha de inicio (formato ISO: yyyy-MM-ddTHH:mm:ss)
- `endDate` (opcional): Fecha de fin (formato ISO: yyyy-MM-ddTHH:mm:ss)

#### Ejemplos de Uso:

**Historial básico paginado:**
```
GET /history?page=0&size=20&sortBy=requestDate&sortDirection=desc
```

**Historial con filtros:**
```
GET /history?page=0&size=10&endpoint=/calculate&httpMethod=POST&responseStatus=200&hasError=false&startDate=2025-10-21T00:00:00&endDate=2025-10-21T23:59:59
```

**Solo requests con errores:**
```
GET /history?hasError=true&page=0&size=50
```

**Requests de un endpoint específico:**
```
GET /history?endpoint=/calculate&page=0&size=100
```

#### Respuesta:
```json
{
    "status": "success",
    "data": [
        {
            "id": 1,
            "requestDate": "2025-10-21T23:30:00",
            "endpoint": "/api/v1/calculate",
            "httpMethod": "POST",
            "requestParameters": null,
            "requestBody": "{\"firstNumber\": 10.50, \"secondNumber\": 20.25}",
            "responseStatus": "200",
            "responseBody": "{\"firstNumber\": 10.50, \"secondNumber\": 20.25, \"percentage\": 15.75, \"result\": 35.59}",
            "errorMessage": null,
            "executionTimeMs": 150,
            "clientIp": "192.168.1.1",
            "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
        }
    ],
    "pagination": {
        "currentPage": 0,
        "totalPages": 1,
        "totalElements": 1,
        "size": 20,
        "first": true,
        "last": true,
        "numberOfElements": 1
    },
    "timestamp": "2025-10-21T23:30:00"
}
```

### 2. **GET /history/stats** - Estadísticas del Historial

Obtiene estadísticas detalladas del historial de requests.

#### Respuesta:
```json
{
    "status": "success",
    "totalRequests": 25,
    "statistics": {
        "requestsByEndpoint": [
            ["/api/v1/calculate", 15],
            ["/api/v1/history", 8],
            ["/api/v1/health", 2]
        ],
        "requestsByHttpMethod": [
            ["POST", 15],
            ["GET", 10]
        ],
        "requestsByResponseStatus": [
            ["200", 22],
            ["400", 2],
            ["500", 1]
        ],
        "executionTimeStats": [
            ["/api/v1/calculate", 150.5, 100, 300],
            ["/api/v1/history", 50.2, 30, 100]
        ]
    },
    "timestamp": "2025-10-21T23:30:00"
}
```

### 3. **POST /history/cleanup** - Limpieza de Registros Antiguos

Elimina registros de historial más antiguos de 30 días (ejecución asíncrona).

#### Respuesta:
```json
{
    "status": "success",
    "message": "Limpieza de registros antiguos iniciada",
    "timestamp": "2025-10-21T23:30:00"
}
```

## 🔍 Campos del Historial

Cada registro de historial incluye la siguiente información:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único del registro |
| `requestDate` | LocalDateTime | Fecha y hora del request |
| `endpoint` | String | Endpoint llamado |
| `httpMethod` | String | Método HTTP (GET, POST, PUT, DELETE) |
| `requestParameters` | String | Parámetros de query string |
| `requestBody` | String | Cuerpo del request (JSON) |
| `responseStatus` | String | Código de respuesta HTTP |
| `responseBody` | String | Cuerpo de la respuesta (JSON) |
| `errorMessage` | String | Mensaje de error (si aplica) |
| `executionTimeMs` | Long | Tiempo de ejecución en milisegundos |
| `clientIp` | String | IP del cliente |
| `userAgent` | String | User Agent del navegador/cliente |

## 🚀 Características Técnicas

### Registro Automático
- **Interceptor HTTP**: Captura automáticamente todos los requests
- **Ejecución Asíncrona**: No afecta el rendimiento de la aplicación
- **Información Completa**: Request, response, timing, IP, User Agent

### Paginación
- **Spring Data JPA**: Paginación nativa con Page y Pageable
- **Límites de Seguridad**: Máximo 100 elementos por página
- **Ordenamiento Flexible**: Por cualquier campo disponible

### Filtros Dinámicos
- **Consultas JPA**: Filtros opcionales con parámetros dinámicos
- **Búsqueda Parcial**: Filtro por endpoint con LIKE
- **Rangos de Fecha**: Filtros temporales precisos

### Manejo de Errores
- **Validación de Fechas**: Formato ISO requerido
- **Respuestas Estructuradas**: Códigos HTTP apropiados
- **Mensajes Descriptivos**: Errores claros y específicos

## 📊 Casos de Uso Comunes

### 1. Monitoreo de Rendimiento
```bash
# Requests más lentos
GET /history?sortBy=executionTimeMs&sortDirection=desc&page=0&size=10

# Requests del último día
GET /history?startDate=2025-10-21T00:00:00&endDate=2025-10-21T23:59:59
```

### 2. Análisis de Errores
```bash
# Solo requests con errores
GET /history?hasError=true&page=0&size=50

# Errores 500 específicos
GET /history?responseStatus=500&hasError=true
```

### 3. Auditoría de Endpoints
```bash
# Todos los requests a /calculate
GET /history?endpoint=/calculate&page=0&size=100

# Requests POST únicamente
GET /history?httpMethod=POST&page=0&size=50
```

### 4. Análisis de Clientes
```bash
# Requests por IP específica (requiere filtro adicional en implementación)
GET /history?page=0&size=100
```

## ⚠️ Consideraciones Importantes

1. **Volumen de Datos**: El historial puede crecer rápidamente, usar paginación
2. **Limpieza Automática**: Los registros se eliminan después de 30 días
3. **Rendimiento**: Los filtros complejos pueden ser más lentos
4. **Privacidad**: El historial incluye IPs y User Agents
5. **Almacenamiento**: Considerar políticas de retención de datos

## 🔧 Configuración

### Variables de Entorno Postman
- `baseUrl`: `http://localhost:8080/api/v1`
- `swaggerUrl`: `http://localhost:8080/swagger-ui.html`

### Headers Recomendados
- `Content-Type`: `application/json`
- `Accept`: `application/json`

## 📈 Estadísticas Disponibles

Las estadísticas incluyen:
- **Requests por Endpoint**: Conteo de llamadas por endpoint
- **Requests por Método HTTP**: Distribución de métodos HTTP
- **Requests por Código de Respuesta**: Distribución de códigos de estado
- **Estadísticas de Tiempo**: Promedio, mínimo y máximo por endpoint

---

**Nota**: Esta documentación está actualizada para la versión actual de la API con los nuevos endpoints de historial detallado.
