package com.tokenlabs.repository;

import com.tokenlabs.model.CalculationHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CalculationHistoryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;
    
    private CalculationHistory testHistory;
    
    @BeforeEach
    void setUp() {
        testHistory = new CalculationHistory(
            new BigDecimal("10.50"),
            new BigDecimal("20.25"),
            new BigDecimal("15.75"),
            new BigDecimal("35.59")
        );
    }
    
    @Test
    void save_ShouldPersistEntity_WhenValidData() {
        // Act
        CalculationHistory saved = calculationHistoryRepository.save(testHistory);
        
        // Assert
        assertNotNull(saved.getId());
        assertEquals(testHistory.getFirstNumber(), saved.getFirstNumber());
        assertEquals(testHistory.getSecondNumber(), saved.getSecondNumber());
        assertEquals(testHistory.getPercentage(), saved.getPercentage());
        assertEquals(testHistory.getResult(), saved.getResult());
        assertNotNull(saved.getCreatedAt());
    }
    
    @Test
    void findAllOrderByCreatedAtDesc_ShouldReturnOrderedList_WhenMultipleRecords() {
        // Arrange
        CalculationHistory history1 = new CalculationHistory(
            new BigDecimal("5.0"), new BigDecimal("10.0"), 
            new BigDecimal("12.5"), new BigDecimal("16.88")
        );
        history1.setCreatedAt(LocalDateTime.now().minusHours(2));
        
        CalculationHistory history2 = new CalculationHistory(
            new BigDecimal("3.0"), new BigDecimal("7.0"), 
            new BigDecimal("8.0"), new BigDecimal("10.80")
        );
        history2.setCreatedAt(LocalDateTime.now().minusHours(1));
        
        entityManager.persistAndFlush(history1);
        entityManager.persistAndFlush(history2);
        
        // Act
        List<CalculationHistory> result = calculationHistoryRepository.findAllOrderByCreatedAtDesc();
        
        // Assert
        assertEquals(2, result.size());
        // Verificar que está ordenado por fecha descendente (más reciente primero)
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()));
    }
    
    @Test
    void countAllCalculations_ShouldReturnCorrectCount_WhenMultipleRecords() {
        // Arrange
        CalculationHistory history1 = new CalculationHistory(
            new BigDecimal("1.0"), new BigDecimal("1.0"), 
            new BigDecimal("10.0"), new BigDecimal("2.20")
        );
        
        CalculationHistory history2 = new CalculationHistory(
            new BigDecimal("2.0"), new BigDecimal("2.0"), 
            new BigDecimal("15.0"), new BigDecimal("4.60")
        );
        
        entityManager.persistAndFlush(history1);
        entityManager.persistAndFlush(history2);
        
        // Act
        Long count = calculationHistoryRepository.countAllCalculations();
        
        // Assert
        assertEquals(2L, count);
    }
    
    @Test
    void save_ShouldSetCreatedAt_WhenEntityIsPersisted() {
        // Arrange
        CalculationHistory history = new CalculationHistory(
            new BigDecimal("7.5"), new BigDecimal("12.5"), 
            new BigDecimal("8.0"), new BigDecimal("21.60")
        );
        
        // Act
        CalculationHistory saved = calculationHistoryRepository.save(history);
        
        // Assert
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(saved.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}
