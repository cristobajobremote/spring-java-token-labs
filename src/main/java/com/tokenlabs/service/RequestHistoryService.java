package com.tokenlabs.service;

import com.tokenlabs.model.RequestHistory;
import com.tokenlabs.repository.RequestHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class RequestHistoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHistoryService.class);
    
    @Autowired
    private RequestHistoryRepository requestHistoryRepository;
    
    /**
     * Registra un request de forma asíncrona
     */
    @Async
    public CompletableFuture<Void> logRequestAsync(String endpoint, String httpMethod, 
                                                   String requestParameters, String requestBody,
                                                   String responseStatus, String responseBody,
                                                   String errorMessage, Long executionTimeMs,
                                                   String clientIp, String userAgent) {
        try {
            RequestHistory requestHistory = new RequestHistory();
            requestHistory.setRequestDate(LocalDateTime.now());
            requestHistory.setEndpoint(endpoint);
            requestHistory.setHttpMethod(httpMethod);
            requestHistory.setRequestParameters(requestParameters);
            requestHistory.setRequestBody(requestBody);
            requestHistory.setResponseStatus(responseStatus);
            requestHistory.setResponseBody(responseBody);
            requestHistory.setErrorMessage(errorMessage);
            requestHistory.setExecutionTimeMs(executionTimeMs);
            requestHistory.setClientIp(clientIp);
            requestHistory.setUserAgent(userAgent);
            
            requestHistoryRepository.save(requestHistory);
            logger.debug("Request logged successfully for endpoint: {}", endpoint);
            
        } catch (Exception e) {
            logger.error("Error logging request for endpoint: {} - Error: {}", endpoint, e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Obtiene el historial de requests con paginación
     */
    public Page<RequestHistory> getRequestHistory(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return requestHistoryRepository.findAllByOrderByRequestDateDesc(pageable);
    }
    
    /**
     * Busca requests con filtros múltiples
     */
    public Page<RequestHistory> searchRequests(String endpoint, String httpMethod, 
                                               String responseStatus, Boolean hasError,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return requestHistoryRepository.findWithFilters(
                endpoint, httpMethod, responseStatus, hasError, 
                startDate, endDate, pageable);
    }
    
    /**
     * Obtiene estadísticas de requests
     */
    public Object getRequestStatistics() {
        return new Object() {
            public final Object requestsByEndpoint = requestHistoryRepository.countRequestsByEndpoint();
            public final Object requestsByHttpMethod = requestHistoryRepository.countRequestsByHttpMethod();
            public final Object requestsByResponseStatus = requestHistoryRepository.countRequestsByResponseStatus();
            public final Object executionTimeStats = requestHistoryRepository.getExecutionTimeStatsByEndpoint();
        };
    }
    
    /**
     * Limpia registros antiguos (más de 30 días)
     */
    @Async
    public CompletableFuture<Integer> cleanOldRecords() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            int deletedCount = requestHistoryRepository.deleteOldRecords(cutoffDate);
            logger.info("Cleaned {} old request records", deletedCount);
            return CompletableFuture.completedFuture(deletedCount);
        } catch (Exception e) {
            logger.error("Error cleaning old records: {}", e.getMessage());
            return CompletableFuture.completedFuture(0);
        }
    }
    
    /**
     * Obtiene el total de requests registrados
     */
    public long getTotalRequests() {
        return requestHistoryRepository.count();
    }
    
    /**
     * Obtiene requests por endpoint
     */
    public Page<RequestHistory> getRequestsByEndpoint(String endpoint, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return requestHistoryRepository.findByEndpointContainingIgnoreCaseOrderByRequestDateDesc(endpoint, pageable);
    }
    
    /**
     * Obtiene requests por método HTTP
     */
    public Page<RequestHistory> getRequestsByHttpMethod(String httpMethod, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return requestHistoryRepository.findByHttpMethodOrderByRequestDateDesc(httpMethod, pageable);
    }
    
    /**
     * Obtiene requests con errores
     */
    public Page<RequestHistory> getRequestsWithErrors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return requestHistoryRepository.findByErrorMessageIsNotNullOrderByRequestDateDesc(pageable);
    }
    
    /**
     * Obtiene requests exitosos
     */
    public Page<RequestHistory> getSuccessfulRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return requestHistoryRepository.findByErrorMessageIsNullOrderByRequestDateDesc(pageable);
    }
}
