package com.tokenlabs.dto;

import java.time.LocalDateTime;

public class RequestHistoryFilter {
    
    private String endpoint;
    private String httpMethod;
    private String responseStatus;
    private Boolean hasError;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "requestDate";
    private String sortDirection = "desc";
    
    // Constructores
    public RequestHistoryFilter() {}
    
    public RequestHistoryFilter(String endpoint, String httpMethod, String responseStatus,
                               Boolean hasError, LocalDateTime startDate, LocalDateTime endDate,
                               Integer page, Integer size, String sortBy, String sortDirection) {
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.responseStatus = responseStatus;
        this.hasError = hasError;
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    // Getters y Setters
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
    
    public String getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    public Boolean getHasError() {
        return hasError;
    }
    
    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestHistoryFilter that = (RequestHistoryFilter) o;
        return java.util.Objects.equals(endpoint, that.endpoint) &&
               java.util.Objects.equals(httpMethod, that.httpMethod) &&
               java.util.Objects.equals(responseStatus, that.responseStatus) &&
               java.util.Objects.equals(hasError, that.hasError) &&
               java.util.Objects.equals(startDate, that.startDate) &&
               java.util.Objects.equals(endDate, that.endDate) &&
               java.util.Objects.equals(page, that.page) &&
               java.util.Objects.equals(size, that.size) &&
               java.util.Objects.equals(sortBy, that.sortBy) &&
               java.util.Objects.equals(sortDirection, that.sortDirection);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(endpoint, httpMethod, responseStatus, hasError, startDate, endDate, page, size, sortBy, sortDirection);
    }
    
    @Override
    public String toString() {
        return "RequestHistoryFilter{" +
                "endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", hasError=" + hasError +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
