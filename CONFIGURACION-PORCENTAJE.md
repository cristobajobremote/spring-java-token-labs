# 🔧 Configuración del Porcentaje

## 📝 Descripción

El porcentaje utilizado en los cálculos ahora es configurable a través del archivo `application.yml`. Esto permite cambiar el valor sin necesidad de modificar el código.

## ⚙️ Configuración Actual

En el archivo `src/main/resources/application.yml`:

```yaml
# Configuración personalizada de la aplicación
app:
  external:
    percentage: 15.75  # Porcentaje configurable para cálculos
```

## 🔄 Cómo Cambiar el Porcentaje

### Opción 1: Modificar application.yml
1. Abre el archivo `src/main/resources/application.yml`
2. Cambia el valor de `app.external.percentage`
3. Reinicia la aplicación

**Ejemplo:**
```yaml
app:
  external:
    percentage: 20.50  # Nuevo porcentaje
```

### Opción 2: Variable de Entorno
Puedes sobrescribir el valor usando una variable de entorno:

```bash
export APP_EXTERNAL_PERCENTAGE=25.0
mvn spring-boot:run
```

### Opción 3: Argumento de Línea de Comandos
```bash
mvn spring-boot:run -Dapp.external.percentage=18.25
```

## 🧪 Verificar el Cambio

Después de cambiar el porcentaje:

1. **Reinicia la aplicación**:
   ```bash
   ./start.sh stop
   ./start.sh run
   ```

2. **Limpia el caché Redis** (opcional):
   ```bash
   redis-cli -p 26379 FLUSHALL
   ```

3. **Prueba el endpoint**:
   ```bash
   curl -X POST http://localhost:8080/api/v1/calculate \
        -H "Content-Type: application/json" \
        -d '{"firstNumber": 10.0, "secondNumber": 20.0}'
   ```

4. **Verifica el porcentaje** en la respuesta:
   ```json
   {
     "firstNumber": 10.0,
     "secondNumber": 20.0,
     "percentage": 15.75,  // ← Este valor debe coincidir con tu configuración
     "result": 34.73,
     "timestamp": "2025-10-21T18:56:50.499404"
   }
   ```

## 📊 Ejemplos de Configuración

### Porcentaje Bajo (5%)
```yaml
app:
  external:
    percentage: 5.0
```

### Porcentaje Medio (15%)
```yaml
app:
  external:
    percentage: 15.0
```

### Porcentaje Alto (30%)
```yaml
app:
  external:
    percentage: 30.0
```

## 🔍 Caché

- El porcentaje se cachea en Redis por **30 minutos**
- Si cambias el porcentaje, necesitas reiniciar la aplicación
- Para cambios inmediatos, limpia el caché Redis: `redis-cli -p 26379 FLUSHALL`

## ⚠️ Notas Importantes

1. **Valor por defecto**: Si no se especifica, el valor por defecto es `15.75`
2. **Formato**: Usa punto decimal (ej: `15.75`, no `15,75`)
3. **Rango**: No hay límites técnicos, pero valores entre 0-100 son recomendables
4. **Reinicio**: Siempre reinicia la aplicación después de cambiar la configuración

## 🐛 Troubleshooting

### El porcentaje no cambia
- Verifica que reiniciaste la aplicación
- Limpia el caché Redis: `redis-cli -p 26379 FLUSHALL`
- Verifica la sintaxis del YAML

### Error de configuración
- Verifica que el formato del número sea correcto (punto decimal)
- Asegúrate de que la indentación del YAML sea correcta
- Revisa los logs de la aplicación para errores de configuración
