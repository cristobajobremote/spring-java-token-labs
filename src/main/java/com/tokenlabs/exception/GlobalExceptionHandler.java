package com.tokenlabs.exception;

import com.tokenlabs.dto.ErrorResponse;
import com.tokenlabs.service.RequestHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private RequestHistoryService requestHistoryService;

    /**
     * Maneja errores de conexión a Redis
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnectionFailure(
            RedisConnectionFailureException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String traceId = generateTraceId();
        logger.error("Error de conexión Redis [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error de conexión al servicio de caché. El servicio está temporalmente no disponible.",
            "RedisConnectionFailure",
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.SERVICE_UNAVAILABLE.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Maneja errores de validación de argumentos de método
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Error de validación [{}]: {}", traceId, ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error de validación en los datos enviados",
            "ValidationError",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        errorResponse.setValidationErrors(validationErrors);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.BAD_REQUEST.value(), traceId);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja errores de violación de restricciones
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Error de restricción [{}]: {}", traceId, ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            validationErrors.add(new ErrorResponse.ValidationError(
                fieldName,
                violation.getInvalidValue(),
                violation.getMessage()
            ));
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error de validación en los parámetros",
            "ConstraintViolation",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        errorResponse.setValidationErrors(validationErrors);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.BAD_REQUEST.value(), traceId);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja errores de tipo de argumento incorrecto
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Error de tipo de argumento [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            String.format("El parámetro '%s' debe ser de tipo %s", 
                ex.getName(), 
                ex.getRequiredType().getSimpleName()),
            "TypeMismatch",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.BAD_REQUEST.value(), traceId);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja errores de parámetros faltantes
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Parámetro faltante [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            String.format("El parámetro requerido '%s' de tipo '%s' no está presente", 
                ex.getParameterName(), ex.getParameterType()),
            "MissingParameter",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.BAD_REQUEST.value(), traceId);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja errores de método HTTP no soportado
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Método HTTP no soportado [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            String.format("El método HTTP '%s' no está soportado para este endpoint. Métodos soportados: %s",
                ex.getMethod(),
                String.join(", ", ex.getSupportedMethods())),
            "MethodNotSupported",
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.METHOD_NOT_ALLOWED.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * Maneja errores de endpoint no encontrado
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("Endpoint no encontrado [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            String.format("El endpoint '%s' no fue encontrado", ex.getRequestURL()),
            "NotFound",
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.NOT_FOUND.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja errores de JSON malformado
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.warn("JSON malformado [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "El JSON enviado está malformado o contiene datos inválidos",
            "MalformedJson",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.BAD_REQUEST.value(), traceId);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja errores de acceso a datos
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.error("Error de acceso a datos [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error interno de acceso a datos. Por favor, intente nuevamente.",
            "DataAccessError",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja errores genéricos de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.error("Error de runtime [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error interno del servidor. Por favor, contacte al administrador.",
            "InternalServerError",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja cualquier excepción no capturada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        String traceId = generateTraceId();
        logger.error("Error no manejado [{}]: {}", traceId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Error interno del servidor. Por favor, contacte al administrador.",
            "UnexpectedError",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            request.getMethod()
        );
        errorResponse.setTraceId(traceId);
        
        // Persistir el error de forma asíncrona
        persistErrorAsync(request, ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), traceId);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Genera un ID único para el trace
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Persiste el error en el historial de requests de forma asíncrona
     */
    private void persistErrorAsync(HttpServletRequest request, Exception ex, int statusCode, String traceId) {
        try {
            // Guardar de forma asíncrona usando el método correcto
            requestHistoryService.logRequestAsync(
                request.getRequestURI(),
                request.getMethod(),
                getRequestParameters(request),
                getRequestBody(request),
                String.valueOf(statusCode),
                null, // No hay response body en caso de error
                String.format("[%s] %s", traceId, ex.getMessage()),
                null, // No calculamos tiempo de ejecución aquí
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
            ).exceptionally(e -> {
                logger.error("Error al persistir historial de error [{}]: {}", traceId, e.getMessage());
                return null;
            });
                
        } catch (Exception e) {
            logger.error("Error al crear historial de error [{}]: {}", traceId, e.getMessage());
        }
    }

    /**
     * Obtiene los parámetros de la request
     */
    private String getRequestParameters(HttpServletRequest request) {
        try {
            StringBuilder params = new StringBuilder("{");
            request.getParameterMap().forEach((key, values) -> {
                params.append("\"").append(key).append("\":\"");
                params.append(String.join(",", values)).append("\",");
            });
            if (params.length() > 1) {
                params.setLength(params.length() - 1); // Remove last comma
            }
            params.append("}");
            return params.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Obtiene el body de la request
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            // En un interceptor real, esto se capturaría en el ContentCachingRequestWrapper
            return null; // Por simplicidad, retornamos null aquí
        } catch (Exception e) {
            return null;
        }
    }
}
