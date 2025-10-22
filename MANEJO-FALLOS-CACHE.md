# 🚨 Manejo de Fallos del Servicio Externo con Caché

## 📝 Descripción

El sistema implementa un mecanismo robusto de caché que maneja fallos del servicio externo de porcentajes:

- **Caché de 30 minutos**: Los porcentajes se almacenan en Redis por 30 minutos
- **Fallback a caché**: Si el servicio externo falla, se usa el último valor almacenado
- **Error controlado**: Si no hay valor en caché y el servicio falla, se devuelve un error

## 🔧 Funcionalidad Implementada

### 1. Servicio Principal (`ExternalPercentageService`)
- Obtiene porcentaje del servicio externo
- Almacena resultado en caché Redis (30 minutos)
- Maneja fallos del servicio externo
- Usa valor en caché como fallback

### 2. Simulador de Fallos (`ExternalServiceFailureSimulator`)
- Permite simular fallos del servicio externo
- Controla el comportamiento de fallo
- Útil para testing y demostración

### 3. Endpoints de Testing
- **POST** `/test/cache-clear` - Limpiar caché
- **POST** `/test/simulate-failure` - Activar simulación de fallo
- **POST** `/test/disable-failure` - Desactivar simulación de fallo
- **GET** `/test/failure-status` - Verificar estado de simulación

## 🧪 Casos de Prueba

### Caso 1: Servicio Externo Funcionando
```bash
# 1. Verificar estado normal
curl -X GET http://localhost:8080/api/v1/test/failure-status

# 2. Realizar cálculo (debería funcionar)
curl -X POST http://localhost:8080/api/v1/calculate \
     -H "Content-Type: application/json" \
     -d '{"firstNumber": 10.0, "secondNumber": 20.0}'

# Respuesta esperada:
# {"firstNumber":10.0,"secondNumber":20.0,"percentage":15.75,"result":34.73,"timestamp":"..."}
```

### Caso 2: Servicio Externo Fallando con Caché Disponible
```bash
# 1. Activar simulación de fallo
curl -X POST http://localhost:8080/api/v1/test/simulate-failure

# 2. Realizar cálculo (debería usar caché)
curl -X POST http://localhost:8080/api/v1/calculate \
     -H "Content-Type: application/json" \
     -d '{"firstNumber": 5.0, "secondNumber": 15.0}'

# Respuesta esperada:
# {"firstNumber":5.0,"secondNumber":15.0,"percentage":15.75,"result":23.15,"timestamp":"..."}
```

### Caso 3: Servicio Externo Fallando sin Caché
```bash
# 1. Limpiar caché
curl -X POST http://localhost:8080/api/v1/test/cache-clear

# 2. Realizar cálculo (debería fallar)
curl -X POST http://localhost:8080/api/v1/calculate \
     -H "Content-Type: application/json" \
     -d '{"firstNumber": 3.0, "secondNumber": 7.0}'

# Respuesta esperada: Error (código diferente a 200)
```

### Caso 4: Recuperación del Servicio
```bash
# 1. Desactivar simulación de fallo
curl -X POST http://localhost:8080/api/v1/test/disable-failure

# 2. Realizar cálculo (debería funcionar normalmente)
curl -X POST http://localhost:8080/api/v1/calculate \
     -H "Content-Type: application/json" \
     -d '{"firstNumber": 8.0, "secondNumber": 12.0}'

# Respuesta esperada:
# {"firstNumber":8.0,"secondNumber":12.0,"percentage":15.75,"result":23.15,"timestamp":"..."}
```

## 📊 Flujo de Funcionamiento

```mermaid
graph TD
    A[Request de Cálculo] --> B[ExternalPercentageService.getPercentage()]
    B --> C{¿Servicio Externo Disponible?}
    C -->|Sí| D[Obtener Porcentaje del Servicio]
    C -->|No| E{¿Hay Valor en Caché?}
    D --> F[Almacenar en Caché Redis]
    E -->|Sí| G[Usar Valor del Caché]
    E -->|No| H[Devolver Error]
    F --> I[Devolver Porcentaje]
    G --> I
    I --> J[Realizar Cálculo]
    H --> K[Error 500]
```

## 🔍 Logs de Ejemplo

### Servicio Funcionando Normalmente
```
INFO  - Obteniendo porcentaje del servicio externo...
INFO  - Servicio externo respondió correctamente con porcentaje: 15.75%
INFO  - Porcentaje obtenido del servicio externo: 15.75%
```

### Servicio Fallando con Caché Disponible
```
INFO  - Obteniendo porcentaje del servicio externo...
ERROR - Error al obtener porcentaje del servicio externo: Servicio externo temporalmente no disponible
INFO  - Usando último valor almacenado en caché: 15.75%
```

### Servicio Fallando sin Caché
```
INFO  - Obteniendo porcentaje del servicio externo...
ERROR - Error al obtener porcentaje del servicio externo: Servicio externo temporalmente no disponible
ERROR - No hay valor en caché y el servicio externo falló
```

## ⚙️ Configuración

### Redis Cache (application.yml)
```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 1800000  # 30 minutos en milisegundos
```

### Porcentaje Configurable
```yaml
app:
  external:
    percentage: 15.75  # Porcentaje por defecto
```

## 🛠️ Troubleshooting

### El caché no funciona
1. Verificar que Redis esté ejecutándose: `redis-cli -p 26379 ping`
2. Revisar logs de conexión a Redis
3. Verificar configuración de caché en `application.yml`

### Los fallos no se simulan
1. Verificar estado: `curl -X GET http://localhost:8080/api/v1/test/failure-status`
2. Activar simulación: `curl -X POST http://localhost:8080/api/v1/test/simulate-failure`
3. Revisar logs de la aplicación

### Error persistente
1. Limpiar caché: `curl -X POST http://localhost:8080/api/v1/test/cache-clear`
2. Desactivar simulación: `curl -X POST http://localhost:8080/api/v1/test/disable-failure`
3. Reiniciar aplicación: `./start.sh stop && ./start.sh run`

## 📈 Beneficios

- **Resiliencia**: El sistema continúa funcionando aunque el servicio externo falle
- **Performance**: Caché reduce latencia en llamadas repetidas
- **Confiabilidad**: Fallback automático a valores conocidos
- **Testing**: Simulación de fallos para validar comportamiento
