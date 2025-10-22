package com.tokenlabs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestHistoryTest {

    private Validator validator;
    private RequestHistory requestHistory;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testDate = LocalDateTime.now();
        requestHistory = new RequestHistory();
        requestHistory.setRequestDate(testDate);
        requestHistory.setEndpoint("/api/test");
        requestHistory.setHttpMethod("GET");
        requestHistory.setRequestParameters("param1=value1");
        requestHistory.setRequestBody("{\"test\": \"data\"}");
        requestHistory.setResponseStatus("200");
        requestHistory.setResponseBody("{\"result\": \"success\"}");
        requestHistory.setExecutionTimeMs(150L);
        requestHistory.setClientIp("192.168.1.1");
        requestHistory.setUserAgent("Mozilla/5.0");
    }

    @Test
    void constructor_ShouldCreateInstanceWithDefaultTimestamp_WhenDefaultConstructorCalled() {
        // Arrange
        RequestHistory history = new RequestHistory();

        // Assert
        assertNotNull(history);
        assertNotNull(history.getRequestDate());
    }

    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenParameterizedConstructorCalled() {
        // Arrange
        String endpoint = "/api/calculate";
        String httpMethod = "POST";
        String requestParams = "firstNumber=10&secondNumber=20";
        String requestBody = "{\"firstNumber\": 10, \"secondNumber\": 20}";
        String responseStatus = "200";
        String responseBody = "{\"result\": 30.0}";

        // Act
        RequestHistory history = new RequestHistory(endpoint, httpMethod, requestParams, 
                                                   requestBody, responseStatus, responseBody);

        // Assert
        assertNotNull(history);
        assertNotNull(history.getRequestDate());
        assertEquals(endpoint, history.getEndpoint());
        assertEquals(httpMethod, history.getHttpMethod());
        assertEquals(requestParams, history.getRequestParameters());
        assertEquals(requestBody, history.getRequestBody());
        assertEquals(responseStatus, history.getResponseStatus());
        assertEquals(responseBody, history.getResponseBody());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        Long newId = 1L;
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        String newEndpoint = "/api/new";
        String newHttpMethod = "PUT";
        String newRequestParams = "id=123";
        String newRequestBody = "{\"id\": 123}";
        String newResponseStatus = "201";
        String newResponseBody = "{\"created\": true}";
        String newErrorMessage = "Test error";
        Long newExecutionTime = 200L;
        String newClientIp = "10.0.0.1";
        String newUserAgent = "Chrome/91.0";

        // Act
        requestHistory.setId(newId);
        requestHistory.setRequestDate(newDate);
        requestHistory.setEndpoint(newEndpoint);
        requestHistory.setHttpMethod(newHttpMethod);
        requestHistory.setRequestParameters(newRequestParams);
        requestHistory.setRequestBody(newRequestBody);
        requestHistory.setResponseStatus(newResponseStatus);
        requestHistory.setResponseBody(newResponseBody);
        requestHistory.setErrorMessage(newErrorMessage);
        requestHistory.setExecutionTimeMs(newExecutionTime);
        requestHistory.setClientIp(newClientIp);
        requestHistory.setUserAgent(newUserAgent);

        // Assert
        assertEquals(newId, requestHistory.getId());
        assertEquals(newDate, requestHistory.getRequestDate());
        assertEquals(newEndpoint, requestHistory.getEndpoint());
        assertEquals(newHttpMethod, requestHistory.getHttpMethod());
        assertEquals(newRequestParams, requestHistory.getRequestParameters());
        assertEquals(newRequestBody, requestHistory.getRequestBody());
        assertEquals(newResponseStatus, requestHistory.getResponseStatus());
        assertEquals(newResponseBody, requestHistory.getResponseBody());
        assertEquals(newErrorMessage, requestHistory.getErrorMessage());
        assertEquals(newExecutionTime, requestHistory.getExecutionTimeMs());
        assertEquals(newClientIp, requestHistory.getClientIp());
        assertEquals(newUserAgent, requestHistory.getUserAgent());
    }

    @Test
    void equals_ShouldReturnTrue_WhenObjectsAreIdentical() {
        // Arrange
        RequestHistory history1 = new RequestHistory();
        history1.setId(1L);
        history1.setRequestDate(testDate);
        history1.setEndpoint("/api/test");

        RequestHistory history2 = new RequestHistory();
        history2.setId(1L);
        history2.setRequestDate(testDate);
        history2.setEndpoint("/api/test");

        // Assert
        assertTrue(history1.equals(history2));
        assertTrue(history2.equals(history1));
        assertEquals(history1.hashCode(), history2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenObjectsAreDifferent() {
        // Arrange
        RequestHistory history1 = new RequestHistory();
        history1.setId(1L);
        history1.setRequestDate(testDate);
        history1.setEndpoint("/api/test");

        RequestHistory history2 = new RequestHistory();
        history2.setId(2L);
        history2.setRequestDate(testDate);
        history2.setEndpoint("/api/test");

        // Assert
        assertFalse(history1.equals(history2));
        assertFalse(history2.equals(history1));
        assertNotEquals(history1.hashCode(), history2.hashCode());
    }

    @Test
    void toString_ShouldReturnExpectedFormat() {
        // Arrange
        requestHistory.setId(1L);
        String expectedString = "RequestHistory{" +
                "id=1, requestDate=" + testDate +
                ", endpoint='/api/test', httpMethod='GET', responseStatus='200', executionTimeMs=150}";
        String actualString = requestHistory.toString();

        // Assert
        assertEquals(expectedString, actualString);
    }

    @Test
    void validation_ShouldPass_WhenValidValuesProvided() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(testDate);
        history.setEndpoint("/api/test");

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldFail_WhenRequestDateIsNull() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(null);
        history.setEndpoint("/api/test");

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertFalse(violations.isEmpty());
        // Verificar que hay al menos una violación relacionada con requestDate
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().contains("requestDate") || 
            v.getMessage().contains("no puede ser nulo")));
    }

    @Test
    void validation_ShouldFail_WhenEndpointIsNull() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(testDate);
        history.setEndpoint(null);

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede ser nulo"));
    }

    @Test
    void validation_ShouldFail_WhenEndpointExceedsMaxLength() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(testDate);
        history.setEndpoint("a".repeat(256)); // Excede el límite de 255 caracteres

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede exceder 255 caracteres"));
    }

    @Test
    void validation_ShouldPass_WhenOptionalFieldsAreNull() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(testDate);
        history.setEndpoint("/api/test");
        history.setHttpMethod(null);
        history.setRequestParameters(null);
        history.setRequestBody(null);
        history.setResponseStatus(null);
        history.setResponseBody(null);
        history.setErrorMessage(null);
        history.setExecutionTimeMs(null);
        history.setClientIp(null);
        history.setUserAgent(null);

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldPass_WhenAllFieldsHaveValues() {
        // Arrange
        RequestHistory history = new RequestHistory();
        history.setRequestDate(testDate);
        history.setEndpoint("/api/test");
        history.setHttpMethod("POST");
        history.setRequestParameters("param=value");
        history.setRequestBody("{\"data\": \"test\"}");
        history.setResponseStatus("200");
        history.setResponseBody("{\"result\": \"success\"}");
        history.setErrorMessage("Test error");
        history.setExecutionTimeMs(100L);
        history.setClientIp("127.0.0.1");
        history.setUserAgent("Test Agent");

        // Act
        Set<ConstraintViolation<RequestHistory>> violations = validator.validate(history);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
