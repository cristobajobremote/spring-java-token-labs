package com.tokenlabs.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenlabs.dto.ErrorResponse;
import com.tokenlabs.service.RequestHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private RequestHistoryService requestHistoryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("TestAgent");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        
        when(requestHistoryService.logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void handleRedisConnectionFailure_ShouldReturnServiceUnavailable() {
        // Arrange
        RedisConnectionFailureException ex = new RedisConnectionFailureException("Redis connection failed");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRedisConnectionFailure(ex, request, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertEquals("Error de conexión al servicio de caché. El servicio está temporalmente no disponible.", errorResponse.getMessage());
        assertEquals("RedisConnectionFailure", errorResponse.getError());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatusCode());
        assertEquals("/api/v1/test", errorResponse.getPath());
        assertEquals("POST", errorResponse.getMethod());
        assertNotNull(errorResponse.getTraceId());
        assertNotNull(errorResponse.getTimestamp());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("calculationRequest", "firstNumber", "10.5", false, null, null, "El valor debe ser mayor que 0");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertEquals("Error de validación en los datos enviados", errorResponse.getMessage());
        assertEquals("ValidationError", errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());
        assertNotNull(errorResponse.getValidationErrors());
        assertEquals(1, errorResponse.getValidationErrors().size());
        assertEquals("firstNumber", errorResponse.getValidationErrors().get(0).getField());
        assertEquals("El valor debe ser mayor que 0", errorResponse.getValidationErrors().get(0).getMessage());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "page", Integer.class, "invalid", null, null);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatchException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertTrue(errorResponse.getMessage().contains("El parámetro 'page' debe ser de tipo Integer"));
        assertEquals("TypeMismatch", errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleHttpRequestMethodNotSupportedException_ShouldReturnMethodNotAllowed() {
        // Arrange
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(
            "DELETE", Arrays.asList("GET", "POST"));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotSupportedException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertTrue(errorResponse.getMessage().contains("El método HTTP 'DELETE' no está soportado"));
        assertTrue(errorResponse.getMessage().contains("Métodos soportados: GET, POST"));
        assertEquals("MethodNotSupported", errorResponse.getError());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleNoHandlerFoundException_ShouldReturnNotFound() {
        // Arrange
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/api/v1/nonexistent", null);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoHandlerFoundException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertTrue(errorResponse.getMessage().contains("El endpoint '/api/v1/nonexistent' no fue encontrado"));
        assertEquals("NotFound", errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturnBadRequest() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertEquals("El JSON enviado está malformado o contiene datos inválidos", errorResponse.getMessage());
        assertEquals("MalformedJson", errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        // Arrange
        RuntimeException ex = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertEquals("Error interno del servidor. Por favor, contacte al administrador.", errorResponse.getMessage());
        assertEquals("InternalServerError", errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("Generic error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("error", errorResponse.getStatus());
        assertEquals("Error interno del servidor. Por favor, contacte al administrador.", errorResponse.getMessage());
        assertEquals("UnexpectedError", errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatusCode());

        // Verify error persistence
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleErrorPersistenceFailure_ShouldNotThrowException() {
        // Arrange
        when(requestHistoryService.logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB Error")));
        RedisConnectionFailureException ex = new RedisConnectionFailureException("Redis connection failed");

        // Act & Assert - Should not throw exception even if persistence fails
        assertDoesNotThrow(() -> {
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleRedisConnectionFailure(ex, request, webRequest);
            assertNotNull(response);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        });

        // Verify persistence was attempted
        verify(requestHistoryService, times(1)).logRequestAsync(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void traceIdGeneration_ShouldBeUnique() {
        // Arrange
        RedisConnectionFailureException ex1 = new RedisConnectionFailureException("Redis connection failed");
        RedisConnectionFailureException ex2 = new RedisConnectionFailureException("Redis connection failed");

        // Act
        ResponseEntity<ErrorResponse> response1 = exceptionHandler.handleRedisConnectionFailure(ex1, request, webRequest);
        ResponseEntity<ErrorResponse> response2 = exceptionHandler.handleRedisConnectionFailure(ex2, request, webRequest);

        // Assert
        assertNotNull(response1.getBody().getTraceId());
        assertNotNull(response2.getBody().getTraceId());
        assertNotEquals(response1.getBody().getTraceId(), response2.getBody().getTraceId());
    }
}
