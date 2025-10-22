package com.tokenlabs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private String status;
    private String message;
    private String error;
    private Integer statusCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private String path;
    private String method;
    private List<ValidationError> validationErrors;
    private String traceId;
    private String requestId;

    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = "error";
    }

    public ErrorResponse(String message, String error, Integer statusCode) {
        this();
        this.message = message;
        this.error = error;
        this.statusCode = statusCode;
    }

    public ErrorResponse(String message, String error, Integer statusCode, String path, String method) {
        this(message, error, statusCode);
        this.path = path;
        this.method = method;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public List<ValidationError> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<ValidationError> validationErrors) { this.validationErrors = validationErrors; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(status, that.status) &&
               Objects.equals(message, that.message) &&
               Objects.equals(error, that.error) &&
               Objects.equals(statusCode, that.statusCode) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(path, that.path) &&
               Objects.equals(method, that.method) &&
               Objects.equals(validationErrors, that.validationErrors) &&
               Objects.equals(traceId, that.traceId) &&
               Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, error, statusCode, timestamp, path, method, validationErrors, traceId, requestId);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
               "status='" + status + '\'' +
               ", message='" + message + '\'' +
               ", error='" + error + '\'' +
               ", statusCode=" + statusCode +
               ", timestamp=" + timestamp +
               ", path='" + path + '\'' +
               ", method='" + method + '\'' +
               ", validationErrors=" + validationErrors +
               ", traceId='" + traceId + '\'' +
               ", requestId='" + requestId + '\'' +
               '}';
    }

    // Inner class for validation errors
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;

        public ValidationError() {}

        public ValidationError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public Object getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        // equals, hashCode, toString
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidationError that = (ValidationError) o;
            return Objects.equals(field, that.field) &&
                   Objects.equals(rejectedValue, that.rejectedValue) &&
                   Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, rejectedValue, message);
        }

        @Override
        public String toString() {
            return "ValidationError{" +
                   "field='" + field + '\'' +
                   ", rejectedValue=" + rejectedValue +
                   ", message='" + message + '\'' +
                   '}';
        }
    }
}
