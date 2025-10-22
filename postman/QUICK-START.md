# 🚀 Guía Rápida de Importación - Postman

## 📥 Importar Colecciones en Postman

### Paso 1: Abrir Postman
- Abre la aplicación Postman en tu computadora
- Si no tienes Postman instalado, descárgalo desde: https://www.postman.com/downloads/

### Paso 2: Importar Colección Principal
1. Haz clic en el botón **"Import"** (esquina superior izquierda)
2. Selecciona **"Files"**
3. Navega a la carpeta `postman/` del proyecto
4. Selecciona: `Spring-Java-Token-Labs-API.postman_collection.json`
5. Haz clic en **"Import"**

### Paso 3: Importar Variables de Entorno
1. Haz clic en **"Import"** nuevamente
2. Selecciona **"Files"**
3. Selecciona: `Spring-Java-Token-Labs-Environment.postman_environment.json`
4. Haz clic en **"Import"**

### Paso 4: Importar Colección de Ejemplos (Opcional)
1. Haz clic en **"Import"** nuevamente
2. Selecciona **"Files"**
3. Selecciona: `Spring-Java-Token-Labs-Test-Examples.postman_collection.json`
4. Haz clic en **"Import"**

### Paso 5: Seleccionar Entorno
1. En la esquina superior derecha, haz clic en el dropdown de entornos
2. Selecciona **"Spring Java Token Labs Environment"**

## ✅ Verificar Configuración

### Variables de Entorno Disponibles:
- `baseUrl`: `http://localhost:8080/api/v1`
- `swaggerUrl`: `http://localhost:8080/swagger-ui.html`

### Prueba Rápida:
1. Selecciona la colección **"Spring Java Token Labs API"**
2. Abre la carpeta **"Health Check"**
3. Ejecuta el request **"Health Check"**
4. Deberías recibir una respuesta con status 200

## 🔧 Solución de Problemas

### Error: "Could not get response"
- Verifica que la aplicación esté ejecutándose: `./start.sh run`
- Verifica que esté en el puerto 8080

### Error: "Connection refused"
- Ejecuta: `./start.sh start-db` para levantar PostgreSQL y Redis
- Luego ejecuta: `./start.sh run` para levantar la aplicación

### Variables no funcionan
- Asegúrate de tener seleccionado el entorno correcto
- Verifica que las variables estén definidas en el entorno

## 📋 Próximos Pasos

### 1. Funcionalidades Básicas
1. **Probar Health Check**: `GET /health`
2. **Realizar un cálculo**: `POST /calculate`
3. **Ver estadísticas**: `GET /stats`

### 2. Historial Detallado
1. **Ver historial paginado**: `GET /history?page=0&size=20`
2. **Aplicar filtros**: `GET /history?endpoint=/calculate&httpMethod=POST`
3. **Ver estadísticas detalladas**: `GET /history/stats`
4. **Probar limpieza**: `POST /history/cleanup`

### 3. Historial de Cálculos
1. **Ver historial básico**: `GET /calculations/history?page=0&size=10`
2. **Ordenar por resultado**: `GET /calculations/history?sortBy=result&sortDirection=desc`
3. **Filtrar por fecha**: `GET /calculations/history?startDate=2025-10-22T00:00:00&endDate=2025-10-22T23:59:59`
4. **Probar paginación**: `GET /calculations/history?page=1&size=5`

### 4. Testing Avanzado
1. **Verificar caché**: Ejecutar múltiples cálculos y verificar que el porcentaje sea el mismo
2. **Simular fallos**: Activar simulación de fallo y probar comportamiento del caché
3. **Monitorear historial**: Verificar que todos los requests se registren automáticamente

## 🎯 Endpoints Principales

### Funcionalidades Básicas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Health check de la API |
| POST | `/calculate` | Realizar cálculo con porcentaje |
| GET | `/stats` | Estadísticas básicas de cálculos |

### Historial Detallado de Requests
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/history` | Historial paginado con filtros avanzados |
| GET | `/history/stats` | Estadísticas detalladas del historial |
| POST | `/history/cleanup` | Limpieza de registros antiguos |

### Historial de Cálculos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/calculations/history` | Historial paginado de todos los cálculos realizados |

### Testing y Simulación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/test/failure-status` | Estado de simulación de fallo |
| POST | `/test/simulate-failure` | Activar simulación de fallo |
| POST | `/test/disable-failure` | Desactivar simulación de fallo |
| POST | `/test/cache-clear` | Limpiar caché de porcentajes |

¡Listo para probar la API! 🚀
