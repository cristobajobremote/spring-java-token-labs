package com.tokenlabs.repository;

import com.tokenlabs.model.CalculationHistory;
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
    
    @Query("SELECT COUNT(ch) FROM CalculationHistory ch")
    Long countAllCalculations();
}
