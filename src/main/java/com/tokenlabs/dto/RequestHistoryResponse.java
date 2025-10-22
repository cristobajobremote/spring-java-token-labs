package com.tokenlabs.dto;

import java.time.LocalDateTime;

public class RequestHistoryResponse {
    
    private Long id;
    private LocalDateTime requestDate;
    private String endpoint;
    private String httpMethod;
    private String requestParameters;
    private String requestBody;
    private String responseStatus;
    private String responseBody;
    private String errorMessage;
    private Long executionTimeMs;
    private String clientIp;
    private String userAgent;
    
    // Constructores
    public RequestHistoryResponse() {}
    
    public RequestHistoryResponse(Long id, LocalDateTime requestDate, String endpoint, 
                                String httpMethod, String requestParameters, String requestBody,
                                String responseStatus, String responseBody, String errorMessage,
                                Long executionTimeMs, String clientIp, String userAgent) {
        this.id = id;
        this.requestDate = requestDate;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.requestParameters = requestParameters;
        this.requestBody = requestBody;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.errorMessage = errorMessage;
        this.executionTimeMs = executionTimeMs;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
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
        RequestHistoryResponse that = (RequestHistoryResponse) o;
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
        return "RequestHistoryResponse{" +
                "id=" + id +
                ", requestDate=" + requestDate +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
