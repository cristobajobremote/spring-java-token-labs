# Spring Java Token Labs

## Descripción
API REST desarrollada en Spring Boot (Java 21) que realiza cálculos con porcentajes obtenidos de servicios externos, incluyendo caché Redis y persistencia en PostgreSQL.

## Características
- ✅ Endpoint para sumar dos números con porcentaje externo
- ✅ Caché Redis con TTL de 30 minutos
- ✅ Persistencia en PostgreSQL con historial de cálculos
- ✅ Documentación con Swagger/OpenAPI
- ✅ Pruebas unitarias con JUnit y Mockito
- ✅ Arquitectura limpia (Controller, Service, Repository)
- ✅ Docker Compose para desarrollo local

## Tecnologías
- **Java 21**
- **Spring Boot 3.2.0**
- **PostgreSQL 15**
- **Redis 7**
- **Maven**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3**
- **JUnit 5 & Mockito**

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/tokenlabs/
│   │   ├── SpringJavaTokenLabsApplication.java
│   │   ├── config/
│   │   │   ├── CacheConfig.java
│   │   │   └── OpenApiConfig.java
│   │   ├── controller/
│   │   │   └── CalculationController.java
│   │   ├── dto/
│   │   │   ├── CalculationRequest.java
│   │   │   └── CalculationResponse.java
│   │   ├── model/
│   │   │   └── CalculationHistory.java
│   │   ├── repository/
│   │   │   └── CalculationHistoryRepository.java
│   │   └── service/
│   │       ├── CalculationService.java
│   │       └── ExternalPercentageService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/tokenlabs/
        ├── SpringJavaTokenLabsApplicationTest.java
        ├── controller/
        │   └── CalculationControllerTest.java
        ├── repository/
        │   └── CalculationHistoryRepositoryTest.java
        └── service/
            ├── CalculationServiceTest.java
            └── ExternalPercentageServiceTest.java
```

## Instalación y Ejecución

### Prerrequisitos
- Java 21
- Maven 3.6+
- Docker y Docker Compose

### Desarrollo Local

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd spring-java-token-labs
```

2. **Levantar servicios con Docker Compose**
```bash
docker-compose up -d postgres redis
```

3. **Compilar y ejecutar la aplicación**
```bash
mvn clean compile
mvn spring-boot:run
```

### Docker Completo

1. **Compilar la aplicación**
```bash
mvn clean package
```

2. **Levantar todos los servicios**
```bash
docker-compose up --build
```

## Endpoints de la API

### Base URL
```
http://localhost:8080/api/v1
```

### Documentación Swagger
```
http://localhost:8080/swagger-ui.html
```

### Endpoints Disponibles

#### 1. Realizar Cálculo
```http
POST /api/v1/calculate
Content-Type: application/json

{
  "firstNumber": 10.50,
  "secondNumber": 20.25
}
```

**Respuesta:**
```json
{
  "firstNumber": 10.50,
  "secondNumber": 20.25,
  "percentage": 15.75,
  "result": 35.59,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 2. Obtener Historial
```http
GET /api/v1/history
```

#### 3. Estadísticas
```http
GET /api/v1/history/stats
```

#### 4. Historial por Rango de Fechas
```http
GET /api/v1/history/range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

#### 5. Health Check
```http
GET /api/v1/health
```

## Configuración

### Variables de Entorno
- `SPRING_PROFILES_ACTIVE`: Perfil activo (dev, docker, test)
- `SPRING_DATASOURCE_URL`: URL de conexión a PostgreSQL
- `SPRING_DATASOURCE_USERNAME`: Usuario de PostgreSQL
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de PostgreSQL
- `SPRING_REDIS_HOST`: Host de Redis
- `SPRING_REDIS_PORT`: Puerto de Redis

### Perfiles
- **dev**: Desarrollo local con PostgreSQL y Redis locales
- **docker**: Configuración para contenedores Docker
- **test**: Configuración para pruebas con H2 en memoria

## Pruebas

### Ejecutar Todas las Pruebas
```bash
mvn test
```

### Ejecutar Pruebas Específicas
```bash
mvn test -Dtest=CalculationServiceTest
```

### Cobertura de Pruebas
```bash
mvn test jacoco:report
```

## Caché

El sistema utiliza Redis para cachear los porcentajes obtenidos del servicio externo:
- **TTL**: 30 minutos
- **Clave**: `percentage::external-percentage`
- **Serialización**: JSON

## Base de Datos

### Tabla: calculation_history
```sql
CREATE TABLE calculation_history (
    id BIGSERIAL PRIMARY KEY,
    first_number DECIMAL(10,2) NOT NULL,
    second_number DECIMAL(10,2) NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    result DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## Monitoreo

### Actuator Endpoints
- `GET /actuator/health` - Estado de salud
- `GET /actuator/info` - Información de la aplicación
- `GET /actuator/metrics` - Métricas de la aplicación
- `GET /actuator/caches` - Estado de los cachés

## Desarrollo

### Estructura de Commits
```
feat: nueva funcionalidad
fix: corrección de bug
docs: documentación
test: pruebas
refactor: refactorización
```

### Estándares de Código
- Java 21 features
- Spring Boot best practices
- Clean Architecture
- Test-driven development
- Comprehensive logging

## Licencia
MIT License

## Contacto
Token Labs Team - contact@tokenlabs.com
