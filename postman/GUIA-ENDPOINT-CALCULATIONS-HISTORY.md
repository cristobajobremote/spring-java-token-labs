# 📊 Guía del Endpoint de Historial de Cálculos

## 🎯 **Endpoint Principal**
```
GET /api/v1/calculations/history
```

## 📋 **Descripción**
Obtiene el historial paginado de todos los cálculos realizados con filtros opcionales y ordenamiento flexible.

## 🔧 **Parámetros de Query**

### **Paginación (Obligatorios)**
| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | int | 0 | Número de página (0-based) |
| `size` | int | 20 | Tamaño de página (1-100) |

### **Ordenamiento (Opcionales)**
| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `sortBy` | string | "createdAt" | Campo para ordenar (id, firstNumber, secondNumber, percentage, result, createdAt) |
| `sortDirection` | string | "desc" | Dirección de ordenamiento (asc/desc) |

### **Filtros de Fecha (Opcionales)**
| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `startDate` | string | null | Fecha de inicio (ISO format: yyyy-MM-ddTHH:mm:ss) |
| `endDate` | string | null | Fecha de fin (ISO format: yyyy-MM-ddTHH:mm:ss) |

## 📝 **Ejemplos de Uso**

### **1. Consulta Básica**
```bash
curl 'http://localhost:8080/api/v1/calculations/history?page=0&size=10'
```

### **2. Con Ordenamiento por Resultado**
```bash
curl 'http://localhost:8080/api/v1/calculations/history?page=0&size=5&sortBy=result&sortDirection=desc'
```

### **3. Con Filtro de Fechas**
```bash
curl 'http://localhost:8080/api/v1/calculations/history?page=0&size=10&startDate=2025-10-22T00:00:00&endDate=2025-10-22T23:59:59'
```

### **4. Paginación Avanzada**
```bash
curl 'http://localhost:8080/api/v1/calculations/history?page=1&size=5&sortBy=createdAt&sortDirection=desc'
```

## 📊 **Respuesta del Endpoint**

### **Estructura de Respuesta**
```json
{
  "status": "success",
  "timestamp": "2025-10-22T12:45:47.271687",
  "pagination": {
    "currentPage": 0,
    "totalPages": 7,
    "totalElements": 62,
    "size": 10,
    "numberOfElements": 10,
    "first": true,
    "last": false
  },
  "data": [
    {
      "id": 59,
      "firstNumber": 10.00,
      "secondNumber": 20.00,
      "percentage": 15.75,
      "result": 34.73,
      "createdAt": "2025-10-22T15:18:16.073194"
    }
  ]
}
```

### **Campos de Paginación**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `currentPage` | int | Página actual (0-based) |
| `totalPages` | int | Total de páginas disponibles |
| `totalElements` | long | Total de elementos en la base de datos |
| `size` | int | Tamaño de página solicitado |
| `numberOfElements` | int | Elementos en la página actual |
| `first` | boolean | Es la primera página |
| `last` | boolean | Es la última página |

### **Campos de Datos**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | long | ID único del cálculo |
| `firstNumber` | BigDecimal | Primer número del cálculo |
| `secondNumber` | BigDecimal | Segundo número del cálculo |
| `percentage` | BigDecimal | Porcentaje aplicado |
| `result` | BigDecimal | Resultado del cálculo |
| `createdAt` | LocalDateTime | Fecha y hora del cálculo |

## ⚠️ **Códigos de Error**

### **400 Bad Request**
```json
{
  "status": "error",
  "message": "El número de página debe ser mayor o igual a 0",
  "timestamp": "2025-10-22T12:45:47.271687"
}
```

### **500 Internal Server Error**
```json
{
  "status": "error",
  "message": "Error interno del servidor: [detalle del error]",
  "timestamp": "2025-10-22T12:45:47.271687"
}
```

## 🧪 **Casos de Prueba en Postman**

### **1. Get Calculations History - Basic**
- **URL**: `{{baseUrl}}/calculations/history?page=0&size=10`
- **Descripción**: Obtener historial básico con paginación por defecto

### **2. Get Calculations History - With Sorting**
- **URL**: `{{baseUrl}}/calculations/history?page=0&size=5&sortBy=result&sortDirection=desc`
- **Descripción**: Obtener historial ordenado por resultado descendente

### **3. Get Calculations History - With Date Filter**
- **URL**: `{{baseUrl}}/calculations/history?page=0&size=10&startDate=2025-10-22T00:00:00&endDate=2025-10-22T23:59:59`
- **Descripción**: Obtener historial filtrado por rango de fechas

### **4. Get Calculations History - Pagination Test**
- **URL**: `{{baseUrl}}/calculations/history?page=1&size=5&sortBy=createdAt&sortDirection=desc`
- **Descripción**: Probar paginación en la segunda página

## 🔍 **Validaciones**

### **Parámetros de Paginación**
- `page` debe ser >= 0
- `size` debe estar entre 1 y 100

### **Formato de Fechas**
- Las fechas deben estar en formato ISO: `yyyy-MM-ddTHH:mm:ss`
- Ejemplo válido: `2025-10-22T15:30:45`

### **Campos de Ordenamiento**
- Campos válidos: `id`, `firstNumber`, `secondNumber`, `percentage`, `result`, `createdAt`
- Direcciones válidas: `asc`, `desc`

## 🚀 **Características Técnicas**

- ✅ **Paginación completa** con metadatos
- ✅ **Ordenamiento flexible** por cualquier campo
- ✅ **Filtros de fecha** opcionales
- ✅ **Validación robusta** de parámetros
- ✅ **Respuesta estructurada** con información de paginación
- ✅ **Manejo de errores** con códigos HTTP apropiados
- ✅ **Documentación Swagger** integrada

## 📈 **Rendimiento**

- **Tiempo de respuesta**: < 100ms para consultas básicas
- **Escalabilidad**: Soporta hasta 100 elementos por página
- **Índices**: Optimizado con índices en `createdAt` y `id`
- **Caché**: No aplica (datos históricos en tiempo real)
