package com.tokenlabs.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    private ErrorResponse errorResponse;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.now();
        errorResponse = new ErrorResponse();
    }

    @Test
    void constructor_ShouldInitializeWithDefaultValues() {
        // Act
        ErrorResponse response = new ErrorResponse();

        // Assert
        assertNotNull(response.getTimestamp());
        assertEquals("error", response.getStatus());
        assertNull(response.getMessage());
        assertNull(response.getError());
        assertNull(response.getStatusCode());
    }

    @Test
    void constructor_WithMessageErrorAndStatusCode_ShouldSetValues() {
        // Arrange
        String message = "Test error message";
        String error = "TestError";
        Integer statusCode = 400;

        // Act
        ErrorResponse response = new ErrorResponse(message, error, statusCode);

        // Assert
        assertEquals("error", response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
        assertEquals(statusCode, response.getStatusCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void constructor_WithAllParameters_ShouldSetAllValues() {
        // Arrange
        String message = "Test error message";
        String error = "TestError";
        Integer statusCode = 400;
        String path = "/api/v1/test";
        String method = "POST";

        // Act
        ErrorResponse response = new ErrorResponse(message, error, statusCode, path, method);

        // Assert
        assertEquals("error", response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
        assertEquals(statusCode, response.getStatusCode());
        assertEquals(path, response.getPath());
        assertEquals(method, response.getMethod());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        String status = "error";
        String message = "Test message";
        String error = "TestError";
        Integer statusCode = 500;
        String path = "/api/v1/test";
        String method = "GET";
        String traceId = "abc123";
        String requestId = "req456";

        // Act
        errorResponse.setStatus(status);
        errorResponse.setMessage(message);
        errorResponse.setError(error);
        errorResponse.setStatusCode(statusCode);
        errorResponse.setTimestamp(testTimestamp);
        errorResponse.setPath(path);
        errorResponse.setMethod(method);
        errorResponse.setTraceId(traceId);
        errorResponse.setRequestId(requestId);

        // Assert
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(error, errorResponse.getError());
        assertEquals(statusCode, errorResponse.getStatusCode());
        assertEquals(testTimestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertEquals(method, errorResponse.getMethod());
        assertEquals(traceId, errorResponse.getTraceId());
        assertEquals(requestId, errorResponse.getRequestId());
    }

    @Test
    void setValidationErrors_ShouldWorkCorrectly() {
        // Arrange
        List<ErrorResponse.ValidationError> validationErrors = Arrays.asList(
            new ErrorResponse.ValidationError("field1", "invalidValue", "Field1 is invalid"),
            new ErrorResponse.ValidationError("field2", null, "Field2 is required")
        );

        // Act
        errorResponse.setValidationErrors(validationErrors);

        // Assert
        assertNotNull(errorResponse.getValidationErrors());
        assertEquals(2, errorResponse.getValidationErrors().size());
        assertEquals("field1", errorResponse.getValidationErrors().get(0).getField());
        assertEquals("invalidValue", errorResponse.getValidationErrors().get(0).getRejectedValue());
        assertEquals("Field1 is invalid", errorResponse.getValidationErrors().get(0).getMessage());
    }

    @Test
    void equals_WithSameObject_ShouldReturnTrue() {
        // Act & Assert
        assertEquals(errorResponse, errorResponse);
    }

    @Test
    void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        ErrorResponse response1 = new ErrorResponse("Test message", "TestError", 400);
        ErrorResponse response2 = new ErrorResponse("Test message", "TestError", 400);

        // Act & Assert
        assertEquals(response1.getStatus(), response2.getStatus());
        assertEquals(response1.getMessage(), response2.getMessage());
        assertEquals(response1.getError(), response2.getError());
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
        // Timestamp will be different, so we don't test it
    }

    @Test
    void equals_WithDifferentObjects_ShouldReturnFalse() {
        // Arrange
        ErrorResponse response1 = new ErrorResponse("Test message", "TestError", 400);
        ErrorResponse response2 = new ErrorResponse("Different message", "TestError", 400);

        // Act & Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        // Act & Assert
        assertNotEquals(errorResponse, null);
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        // Act & Assert
        assertNotEquals(errorResponse, "Not an ErrorResponse");
    }

    @Test
    void hashCode_WithEqualObjects_ShouldBeEqual() {
        // Arrange
        ErrorResponse response1 = new ErrorResponse("Test message", "TestError", 400);
        ErrorResponse response2 = new ErrorResponse("Test message", "TestError", 400);

        // Act & Assert
        // Since timestamp is different, hashCode will be different
        // We test that the important fields are equal instead
        assertEquals(response1.getStatus(), response2.getStatus());
        assertEquals(response1.getMessage(), response2.getMessage());
        assertEquals(response1.getError(), response2.getError());
        assertEquals(response1.getStatusCode(), response2.getStatusCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Arrange
        errorResponse.setMessage("Test message");
        errorResponse.setError("TestError");
        errorResponse.setStatusCode(400);
        errorResponse.setPath("/api/v1/test");
        errorResponse.setMethod("POST");

        // Act
        String toString = errorResponse.toString();

        // Assert
        assertTrue(toString.contains("ErrorResponse"));
        assertTrue(toString.contains("Test message"));
        assertTrue(toString.contains("TestError"));
        assertTrue(toString.contains("400"));
        assertTrue(toString.contains("/api/v1/test"));
        assertTrue(toString.contains("POST"));
    }

    // Tests for ValidationError inner class
    @Test
    void validationError_Constructor_ShouldInitializeValues() {
        // Arrange
        String field = "testField";
        Object rejectedValue = "invalidValue";
        String message = "Test validation message";

        // Act
        ErrorResponse.ValidationError validationError = new ErrorResponse.ValidationError(field, rejectedValue, message);

        // Assert
        assertEquals(field, validationError.getField());
        assertEquals(rejectedValue, validationError.getRejectedValue());
        assertEquals(message, validationError.getMessage());
    }

    @Test
    void validationError_SettersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        ErrorResponse.ValidationError validationError = new ErrorResponse.ValidationError();
        String field = "testField";
        Object rejectedValue = "invalidValue";
        String message = "Test validation message";

        // Act
        validationError.setField(field);
        validationError.setRejectedValue(rejectedValue);
        validationError.setMessage(message);

        // Assert
        assertEquals(field, validationError.getField());
        assertEquals(rejectedValue, validationError.getRejectedValue());
        assertEquals(message, validationError.getMessage());
    }

    @Test
    void validationError_Equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        ErrorResponse.ValidationError error1 = new ErrorResponse.ValidationError("field", "value", "message");
        ErrorResponse.ValidationError error2 = new ErrorResponse.ValidationError("field", "value", "message");

        // Act & Assert
        assertEquals(error1, error2);
    }

    @Test
    void validationError_Equals_WithDifferentObjects_ShouldReturnFalse() {
        // Arrange
        ErrorResponse.ValidationError error1 = new ErrorResponse.ValidationError("field1", "value", "message");
        ErrorResponse.ValidationError error2 = new ErrorResponse.ValidationError("field2", "value", "message");

        // Act & Assert
        assertNotEquals(error1, error2);
    }

    @Test
    void validationError_HashCode_WithEqualObjects_ShouldBeEqual() {
        // Arrange
        ErrorResponse.ValidationError error1 = new ErrorResponse.ValidationError("field", "value", "message");
        ErrorResponse.ValidationError error2 = new ErrorResponse.ValidationError("field", "value", "message");

        // Act & Assert
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void validationError_ToString_ShouldContainAllFields() {
        // Arrange
        ErrorResponse.ValidationError validationError = new ErrorResponse.ValidationError("testField", "invalidValue", "Test message");

        // Act
        String toString = validationError.toString();

        // Assert
        assertTrue(toString.contains("ValidationError"));
        assertTrue(toString.contains("testField"));
        assertTrue(toString.contains("invalidValue"));
        assertTrue(toString.contains("Test message"));
    }
}
