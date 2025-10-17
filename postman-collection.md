# Ejemplo de colección de Postman para Spring Java Token Labs API

## Variables de Entorno
- `baseUrl`: http://localhost:8080/api/v1
- `swaggerUrl`: http://localhost:8080/swagger-ui.html

## Colección: Spring Java Token Labs API

### 1. Health Check
```http
GET {{baseUrl}}/health
```

### 2. Realizar Cálculo
```http
POST {{baseUrl}}/calculate
Content-Type: application/json

{
  "firstNumber": 10.50,
  "secondNumber": 20.25
}
```

### 3. Obtener Historial Completo
```http
GET {{baseUrl}}/history
```

### 4. Obtener Estadísticas
```http
GET {{baseUrl}}/history/stats
```

### 5. Historial por Rango de Fechas
```http
GET {{baseUrl}}/history/range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

### 6. Actuator Health
```http
GET http://localhost:8080/actuator/health
```

### 7. Actuator Metrics
```http
GET http://localhost:8080/actuator/metrics
```

### 8. Actuator Caches
```http
GET http://localhost:8080/actuator/caches
```

## Ejemplos de Pruebas

### Prueba con Valores Positivos
```json
{
  "firstNumber": 15.75,
  "secondNumber": 25.50
}
```

### Prueba con Valores Decimales
```json
{
  "firstNumber": 0.50,
  "secondNumber": 0.25
}
```

### Prueba con Valores Grandes
```json
{
  "firstNumber": 1000.00,
  "secondNumber": 2000.00
}
```

### Prueba de Validación (debería fallar)
```json
{
  "firstNumber": -5.0,
  "secondNumber": 10.0
}
```

## Scripts de Prueba para Postman

### Test para Health Check
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has status field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql("OK");
});
```

### Test para Cálculo
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has all required fields", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('firstNumber');
    pm.expect(jsonData).to.have.property('secondNumber');
    pm.expect(jsonData).to.have.property('percentage');
    pm.expect(jsonData).to.have.property('result');
    pm.expect(jsonData).to.have.property('timestamp');
});

pm.test("Result is greater than sum", function () {
    var jsonData = pm.response.json();
    var sum = parseFloat(jsonData.firstNumber) + parseFloat(jsonData.secondNumber);
    pm.expect(parseFloat(jsonData.result)).to.be.above(sum);
});
```

### Test para Historial
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});
```
