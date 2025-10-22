package com.tokenlabs.repository;

import com.tokenlabs.model.CalculationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    
    @Query("SELECT ch FROM CalculationHistory ch ORDER BY ch.createdAt DESC")
    List<CalculationHistory> findAllOrderByCreatedAtDesc();
    
    List<CalculationHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Métodos paginados
    Page<CalculationHistory> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Page<CalculationHistory> findByCreatedAtAfter(LocalDateTime startDate, Pageable pageable);
    
    Page<CalculationHistory> findByCreatedAtBefore(LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT COUNT(ch) FROM CalculationHistory ch")
    Long countAllCalculations();
}
