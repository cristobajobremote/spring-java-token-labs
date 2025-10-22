package com.tokenlabs.repository;

import com.tokenlabs.model.RequestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {
    
    /**
     * Busca el historial de requests ordenado por fecha descendente con paginación
     */
    Page<RequestHistory> findAllByOrderByRequestDateDesc(Pageable pageable);
    
    /**
     * Busca el historial de requests por endpoint con paginación
     */
    Page<RequestHistory> findByEndpointContainingIgnoreCaseOrderByRequestDateDesc(
            String endpoint, Pageable pageable);
    
    /**
     * Busca el historial de requests por rango de fechas con paginación
     */
    Page<RequestHistory> findByRequestDateBetweenOrderByRequestDateDesc(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Busca el historial de requests por método HTTP con paginación
     */
    Page<RequestHistory> findByHttpMethodOrderByRequestDateDesc(
            String httpMethod, Pageable pageable);
    
    /**
     * Busca el historial de requests por código de respuesta con paginación
     */
    Page<RequestHistory> findByResponseStatusOrderByRequestDateDesc(
            String responseStatus, Pageable pageable);
    
    /**
     * Busca el historial de requests que contienen errores con paginación
     */
    Page<RequestHistory> findByErrorMessageIsNotNullOrderByRequestDateDesc(Pageable pageable);
    
    /**
     * Busca el historial de requests exitosos con paginación
     */
    Page<RequestHistory> findByErrorMessageIsNullOrderByRequestDateDesc(Pageable pageable);
    
    /**
     * Cuenta el total de requests por endpoint
     */
    @Query("SELECT r.endpoint, COUNT(r) FROM RequestHistory r GROUP BY r.endpoint ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByEndpoint();
    
    /**
     * Cuenta el total de requests por método HTTP
     */
    @Query("SELECT r.httpMethod, COUNT(r) FROM RequestHistory r GROUP BY r.httpMethod ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByHttpMethod();
    
    /**
     * Cuenta el total de requests por código de respuesta
     */
    @Query("SELECT r.responseStatus, COUNT(r) FROM RequestHistory r GROUP BY r.responseStatus ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByResponseStatus();
    
    /**
     * Obtiene estadísticas de tiempo de ejecución por endpoint
     */
    @Query("SELECT r.endpoint, AVG(r.executionTimeMs), MIN(r.executionTimeMs), MAX(r.executionTimeMs) " +
           "FROM RequestHistory r WHERE r.executionTimeMs IS NOT NULL GROUP BY r.endpoint")
    List<Object[]> getExecutionTimeStatsByEndpoint();
    
    /**
     * Busca requests con filtros múltiples
     */
    @Query("SELECT r FROM RequestHistory r WHERE " +
           "(:endpoint IS NULL OR LOWER(r.endpoint) LIKE LOWER(CONCAT('%', :endpoint, '%'))) AND " +
           "(:httpMethod IS NULL OR r.httpMethod = :httpMethod) AND " +
           "(:responseStatus IS NULL OR r.responseStatus = :responseStatus) AND " +
           "(:hasError IS NULL OR (:hasError = true AND r.errorMessage IS NOT NULL) OR (:hasError = false AND r.errorMessage IS NULL)) AND " +
           "(:startDate IS NULL OR r.requestDate >= :startDate) AND " +
           "(:endDate IS NULL OR r.requestDate <= :endDate) " +
           "ORDER BY r.requestDate DESC")
    Page<RequestHistory> findWithFilters(
            @Param("endpoint") String endpoint,
            @Param("httpMethod") String httpMethod,
            @Param("responseStatus") String responseStatus,
            @Param("hasError") Boolean hasError,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Elimina registros antiguos (más de X días)
     */
    @Query("DELETE FROM RequestHistory r WHERE r.requestDate < :cutoffDate")
    int deleteOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
}
