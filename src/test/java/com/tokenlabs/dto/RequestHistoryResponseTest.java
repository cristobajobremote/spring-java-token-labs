package com.tokenlabs.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RequestHistoryResponseTest {

    private RequestHistoryResponse requestHistoryResponse;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        requestHistoryResponse = new RequestHistoryResponse();
        requestHistoryResponse.setId(1L);
        requestHistoryResponse.setRequestDate(testDate);
        requestHistoryResponse.setEndpoint("/api/test");
        requestHistoryResponse.setHttpMethod("GET");
        requestHistoryResponse.setRequestParameters("param=value");
        requestHistoryResponse.setRequestBody("{\"test\": \"data\"}");
        requestHistoryResponse.setResponseStatus("200");
        requestHistoryResponse.setResponseBody("{\"result\": \"success\"}");
        requestHistoryResponse.setErrorMessage(null);
        requestHistoryResponse.setExecutionTimeMs(150L);
        requestHistoryResponse.setClientIp("192.168.1.1");
        requestHistoryResponse.setUserAgent("Mozilla/5.0");
    }

    @Test
    void constructor_ShouldCreateInstanceWithDefaultValues_WhenDefaultConstructorCalled() {
        // Arrange
        RequestHistoryResponse response = new RequestHistoryResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getRequestDate());
        assertNull(response.getEndpoint());
    }

    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenParameterizedConstructorCalled() {
        // Arrange
        Long id = 1L;
        LocalDateTime date = LocalDateTime.now();
        String endpoint = "/api/calculate";
        String httpMethod = "POST";
        String requestParams = "firstNumber=10&secondNumber=20";
        String requestBody = "{\"firstNumber\": 10, \"secondNumber\": 20}";
        String responseStatus = "200";
        String responseBody = "{\"result\": 30.0}";
        String errorMessage = null;
        Long executionTime = 150L;
        String clientIp = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        // Act
        RequestHistoryResponse response = new RequestHistoryResponse(
                id, date, endpoint, httpMethod, requestParams, requestBody,
                responseStatus, responseBody, errorMessage, executionTime,
                clientIp, userAgent
        );

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(date, response.getRequestDate());
        assertEquals(endpoint, response.getEndpoint());
        assertEquals(httpMethod, response.getHttpMethod());
        assertEquals(requestParams, response.getRequestParameters());
        assertEquals(requestBody, response.getRequestBody());
        assertEquals(responseStatus, response.getResponseStatus());
        assertEquals(responseBody, response.getResponseBody());
        assertEquals(errorMessage, response.getErrorMessage());
        assertEquals(executionTime, response.getExecutionTimeMs());
        assertEquals(clientIp, response.getClientIp());
        assertEquals(userAgent, response.getUserAgent());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        Long newId = 2L;
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
        requestHistoryResponse.setId(newId);
        requestHistoryResponse.setRequestDate(newDate);
        requestHistoryResponse.setEndpoint(newEndpoint);
        requestHistoryResponse.setHttpMethod(newHttpMethod);
        requestHistoryResponse.setRequestParameters(newRequestParams);
        requestHistoryResponse.setRequestBody(newRequestBody);
        requestHistoryResponse.setResponseStatus(newResponseStatus);
        requestHistoryResponse.setResponseBody(newResponseBody);
        requestHistoryResponse.setErrorMessage(newErrorMessage);
        requestHistoryResponse.setExecutionTimeMs(newExecutionTime);
        requestHistoryResponse.setClientIp(newClientIp);
        requestHistoryResponse.setUserAgent(newUserAgent);

        // Assert
        assertEquals(newId, requestHistoryResponse.getId());
        assertEquals(newDate, requestHistoryResponse.getRequestDate());
        assertEquals(newEndpoint, requestHistoryResponse.getEndpoint());
        assertEquals(newHttpMethod, requestHistoryResponse.getHttpMethod());
        assertEquals(newRequestParams, requestHistoryResponse.getRequestParameters());
        assertEquals(newRequestBody, requestHistoryResponse.getRequestBody());
        assertEquals(newResponseStatus, requestHistoryResponse.getResponseStatus());
        assertEquals(newResponseBody, requestHistoryResponse.getResponseBody());
        assertEquals(newErrorMessage, requestHistoryResponse.getErrorMessage());
        assertEquals(newExecutionTime, requestHistoryResponse.getExecutionTimeMs());
        assertEquals(newClientIp, requestHistoryResponse.getClientIp());
        assertEquals(newUserAgent, requestHistoryResponse.getUserAgent());
    }

    @Test
    void equals_ShouldReturnTrue_WhenObjectsAreIdentical() {
        // Arrange
        RequestHistoryResponse response1 = new RequestHistoryResponse();
        response1.setId(1L);
        response1.setRequestDate(testDate);
        response1.setEndpoint("/api/test");

        RequestHistoryResponse response2 = new RequestHistoryResponse();
        response2.setId(1L);
        response2.setRequestDate(testDate);
        response2.setEndpoint("/api/test");

        // Assert
        assertTrue(response1.equals(response2));
        assertTrue(response2.equals(response1));
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenObjectsAreDifferent() {
        // Arrange
        RequestHistoryResponse response1 = new RequestHistoryResponse();
        response1.setId(1L);
        response1.setRequestDate(testDate);
        response1.setEndpoint("/api/test");

        RequestHistoryResponse response2 = new RequestHistoryResponse();
        response2.setId(2L);
        response2.setRequestDate(testDate);
        response2.setEndpoint("/api/test");

        // Assert
        assertFalse(response1.equals(response2));
        assertFalse(response2.equals(response1));
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedWithNull() {
        // Arrange
        RequestHistoryResponse response = new RequestHistoryResponse();

        // Assert
        assertFalse(response.equals(null));
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedWithDifferentClass() {
        // Arrange
        RequestHistoryResponse response = new RequestHistoryResponse();
        String differentObject = "not a RequestHistoryResponse";

        // Assert
        assertFalse(response.equals(differentObject));
    }

    @Test
    void toString_ShouldReturnExpectedFormat() {
        // Arrange
        requestHistoryResponse.setId(1L);
        String expectedString = "RequestHistoryResponse{" +
                "id=1, requestDate=" + testDate +
                ", endpoint='/api/test', httpMethod='GET', responseStatus='200', executionTimeMs=150}";
        String actualString = requestHistoryResponse.toString();

        // Assert
        assertEquals(expectedString, actualString);
    }

    @Test
    void hashCode_ShouldReturnSameValue_WhenCalledMultipleTimes() {
        // Act
        int hashCode1 = requestHistoryResponse.hashCode();
        int hashCode2 = requestHistoryResponse.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_ShouldReturnDifferentValues_WhenObjectsAreDifferent() {
        // Arrange
        RequestHistoryResponse response1 = new RequestHistoryResponse();
        response1.setId(1L);
        response1.setRequestDate(testDate);
        response1.setEndpoint("/api/test");

        RequestHistoryResponse response2 = new RequestHistoryResponse();
        response2.setId(2L);
        response2.setRequestDate(testDate);
        response2.setEndpoint("/api/test");

        // Act
        int hashCode1 = response1.hashCode();
        int hashCode2 = response2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void allFields_ShouldBeNullable_WhenNotSet() {
        // Arrange
        RequestHistoryResponse response = new RequestHistoryResponse();

        // Assert
        assertNull(response.getId());
        assertNull(response.getRequestDate());
        assertNull(response.getEndpoint());
        assertNull(response.getHttpMethod());
        assertNull(response.getRequestParameters());
        assertNull(response.getRequestBody());
        assertNull(response.getResponseStatus());
        assertNull(response.getResponseBody());
        assertNull(response.getErrorMessage());
        assertNull(response.getExecutionTimeMs());
        assertNull(response.getClientIp());
        assertNull(response.getUserAgent());
    }

    @Test
    void allFields_ShouldAcceptNullValues_WhenSetToNull() {
        // Act
        requestHistoryResponse.setId(null);
        requestHistoryResponse.setRequestDate(null);
        requestHistoryResponse.setEndpoint(null);
        requestHistoryResponse.setHttpMethod(null);
        requestHistoryResponse.setRequestParameters(null);
        requestHistoryResponse.setRequestBody(null);
        requestHistoryResponse.setResponseStatus(null);
        requestHistoryResponse.setResponseBody(null);
        requestHistoryResponse.setErrorMessage(null);
        requestHistoryResponse.setExecutionTimeMs(null);
        requestHistoryResponse.setClientIp(null);
        requestHistoryResponse.setUserAgent(null);

        // Assert
        assertNull(requestHistoryResponse.getId());
        assertNull(requestHistoryResponse.getRequestDate());
        assertNull(requestHistoryResponse.getEndpoint());
        assertNull(requestHistoryResponse.getHttpMethod());
        assertNull(requestHistoryResponse.getRequestParameters());
        assertNull(requestHistoryResponse.getRequestBody());
        assertNull(requestHistoryResponse.getResponseStatus());
        assertNull(requestHistoryResponse.getResponseBody());
        assertNull(requestHistoryResponse.getErrorMessage());
        assertNull(requestHistoryResponse.getExecutionTimeMs());
        assertNull(requestHistoryResponse.getClientIp());
        assertNull(requestHistoryResponse.getUserAgent());
    }
}
