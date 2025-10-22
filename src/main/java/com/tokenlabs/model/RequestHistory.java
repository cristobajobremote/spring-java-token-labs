package com.tokenlabs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_history")
public class RequestHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La fecha no puede ser nula")
    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;
    
    @NotNull(message = "El endpoint no puede ser nulo")
    @Size(max = 255, message = "El endpoint no puede exceder 255 caracteres")
    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;
    
    @Column(name = "http_method", length = 10)
    private String httpMethod;
    
    @Column(name = "request_parameters", columnDefinition = "TEXT")
    private String requestParameters;
    
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;
    
    @Column(name = "response_status", length = 10)
    private String responseStatus;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "client_ip", length = 45)
    private String clientIp;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
    }
    
    // Constructores
    public RequestHistory() {
        this.requestDate = LocalDateTime.now();
    }
    
    public RequestHistory(String endpoint, String httpMethod, String requestParameters, 
                         String requestBody, String responseStatus, String responseBody) {
        this.requestDate = LocalDateTime.now();
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.requestParameters = requestParameters;
        this.requestBody = requestBody;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public String getRequestParameters() {
        return requestParameters;
    }
    
    public void setRequestParameters(String requestParameters) {
        this.requestParameters = requestParameters;
    }
    
    public String getRequestBody() {
        return requestBody;
    }
    
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    
    public String getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestHistory that = (RequestHistory) o;
        return java.util.Objects.equals(id, that.id) &&
               java.util.Objects.equals(requestDate, that.requestDate) &&
               java.util.Objects.equals(endpoint, that.endpoint);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, requestDate, endpoint);
    }
    
    @Override
    public String toString() {
        return "RequestHistory{" +
                "id=" + id +
                ", requestDate=" + requestDate +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
