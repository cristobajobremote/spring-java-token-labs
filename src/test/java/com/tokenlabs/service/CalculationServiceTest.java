package com.tokenlabs.service;

import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.repository.CalculationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {
    
    @Mock
    private ExternalPercentageService externalPercentageService;
    
    @Mock
    private CalculationHistoryRepository calculationHistoryRepository;
    
    @InjectMocks
    private CalculationService calculationService;
    
    private CalculationRequest testRequest;
    private BigDecimal testPercentage;
    
    @BeforeEach
    void setUp() {
        testRequest = new CalculationRequest(
            new BigDecimal("10.50"),
            new BigDecimal("20.25")
        );
        testPercentage = new BigDecimal("15.75");
    }
    
    @Test
    void calculate_ShouldReturnCorrectResult_WhenValidRequest() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenReturn(testPercentage);
        when(calculationHistoryRepository.save(any(CalculationHistory.class)))
            .thenAnswer(invocation -> {
                CalculationHistory history = invocation.getArgument(0);
                history.setId(1L);
                return history;
            });
        
        // Act
        CalculationResponse response = calculationService.calculate(testRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(testRequest.getFirstNumber(), response.getFirstNumber());
        assertEquals(testRequest.getSecondNumber(), response.getSecondNumber());
        assertEquals(testPercentage, response.getPercentage());
        
        // Verificar cálculo: (10.50 + 20.25) + ((10.50 + 20.25) * 15.75 / 100)
        BigDecimal expectedSum = new BigDecimal("30.75");
        BigDecimal expectedPercentageAmount = new BigDecimal("4.84"); // 30.75 * 15.75 / 100 = 4.84
        BigDecimal expectedResult = new BigDecimal("35.59"); // 30.75 + 4.84 = 35.59
        
        assertEquals(expectedSum, testRequest.getFirstNumber().add(testRequest.getSecondNumber()));
        assertEquals(expectedResult, response.getResult());
        
        verify(externalPercentageService).getPercentage();
        verify(calculationHistoryRepository).save(any(CalculationHistory.class));
    }
    
    @Test
    void calculate_ShouldSaveToHistory_WhenCalculationSuccessful() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenReturn(testPercentage);
        when(calculationHistoryRepository.save(any(CalculationHistory.class)))
            .thenAnswer(invocation -> {
                CalculationHistory history = invocation.getArgument(0);
                history.setId(1L);
                return history;
            });
        
        // Act
        calculationService.calculate(testRequest);
        
        // Assert
        verify(calculationHistoryRepository).save(argThat(history -> 
            history.getFirstNumber().equals(testRequest.getFirstNumber()) &&
            history.getSecondNumber().equals(testRequest.getSecondNumber()) &&
            history.getPercentage().equals(testPercentage) &&
            history.getResult() != null
        ));
    }
    
    @Test
    void getCalculationHistory_ShouldReturnOrderedList_WhenHistoryExists() {
        // Arrange
        List<CalculationHistory> mockHistory = Arrays.asList(
            createMockHistory(1L, new BigDecimal("5.0"), new BigDecimal("10.0"), 
                            new BigDecimal("12.5"), new BigDecimal("16.88")),
            createMockHistory(2L, new BigDecimal("3.0"), new BigDecimal("7.0"), 
                            new BigDecimal("8.0"), new BigDecimal("10.80"))
        );
        
        when(calculationHistoryRepository.findAllOrderByCreatedAtDesc()).thenReturn(mockHistory);
        
        // Act
        List<CalculationHistory> result = calculationService.getCalculationHistory();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockHistory, result);
        verify(calculationHistoryRepository).findAllOrderByCreatedAtDesc();
    }
    
    @Test
    void getTotalCalculations_ShouldReturnCount_WhenCalled() {
        // Arrange
        Long expectedCount = 5L;
        when(calculationHistoryRepository.countAllCalculations()).thenReturn(expectedCount);
        
        // Act
        Long result = calculationService.getTotalCalculations();
        
        // Assert
        assertEquals(expectedCount, result);
        verify(calculationHistoryRepository).countAllCalculations();
    }
    
    @Test
    void getCalculationHistoryByDateRange_ShouldReturnFilteredList_WhenValidRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<CalculationHistory> mockHistory = Arrays.asList(
            createMockHistory(1L, new BigDecimal("1.0"), new BigDecimal("2.0"), 
                            new BigDecimal("10.0"), new BigDecimal("3.30"))
        );
        
        when(calculationHistoryRepository.findByCreatedAtBetween(startDate, endDate))
            .thenReturn(mockHistory);
        
        // Act
        List<CalculationHistory> result = calculationService.getCalculationHistoryByDateRange(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockHistory, result);
        verify(calculationHistoryRepository).findByCreatedAtBetween(startDate, endDate);
    }
    
    @Test
    void calculate_ShouldHandleLargeValues_WhenRequestContainsLargeNumbers() {
        // Arrange
        CalculationRequest largeRequest = new CalculationRequest(
            new BigDecimal("999999999.99"),
            new BigDecimal("888888888.88")
        );
        BigDecimal percentage = new BigDecimal("25.5");
        
        when(externalPercentageService.getPercentage()).thenReturn(percentage);
        when(calculationHistoryRepository.save(any(CalculationHistory.class)))
            .thenAnswer(invocation -> {
                CalculationHistory history = invocation.getArgument(0);
                history.setId(1L);
                return history;
            });
        
        // Act
        CalculationResponse response = calculationService.calculate(largeRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(largeRequest.getFirstNumber(), response.getFirstNumber());
        assertEquals(largeRequest.getSecondNumber(), response.getSecondNumber());
        assertEquals(percentage, response.getPercentage());
        assertTrue(response.getResult().compareTo(BigDecimal.ZERO) > 0);
        
        verify(externalPercentageService).getPercentage();
        verify(calculationHistoryRepository).save(any(CalculationHistory.class));
    }
    
    @Test
    void calculate_ShouldHandleDecimalPrecision_WhenRequestContainsComplexDecimals() {
        // Arrange
        CalculationRequest decimalRequest = new CalculationRequest(
            new BigDecimal("123.456789"),
            new BigDecimal("987.654321")
        );
        BigDecimal percentage = new BigDecimal("12.345");
        
        when(externalPercentageService.getPercentage()).thenReturn(percentage);
        when(calculationHistoryRepository.save(any(CalculationHistory.class)))
            .thenAnswer(invocation -> {
                CalculationHistory history = invocation.getArgument(0);
                history.setId(1L);
                return history;
            });
        
        // Act
        CalculationResponse response = calculationService.calculate(decimalRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(decimalRequest.getFirstNumber(), response.getFirstNumber());
        assertEquals(decimalRequest.getSecondNumber(), response.getSecondNumber());
        assertEquals(percentage, response.getPercentage());
        assertNotNull(response.getResult());
        
        verify(externalPercentageService).getPercentage();
        verify(calculationHistoryRepository).save(any(CalculationHistory.class));
    }
    
    @Test
    void getCalculationHistory_ShouldReturnEmptyList_WhenNoHistoryExists() {
        // Arrange
        when(calculationHistoryRepository.findAllOrderByCreatedAtDesc()).thenReturn(Arrays.asList());
        
        // Act
        List<CalculationHistory> result = calculationService.getCalculationHistory();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(calculationHistoryRepository).findAllOrderByCreatedAtDesc();
    }
    
    @Test
    void getTotalCalculations_ShouldReturnZero_WhenNoCalculationsExist() {
        // Arrange
        when(calculationHistoryRepository.countAllCalculations()).thenReturn(0L);
        
        // Act
        Long result = calculationService.getTotalCalculations();
        
        // Assert
        assertEquals(0L, result);
        verify(calculationHistoryRepository).countAllCalculations();
    }
    
    @Test
    void getCalculationHistoryByDateRange_ShouldReturnEmptyList_WhenNoRecordsInRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(calculationHistoryRepository.findByCreatedAtBetween(startDate, endDate))
            .thenReturn(Arrays.asList());
        
        // Act
        List<CalculationHistory> result = calculationService.getCalculationHistoryByDateRange(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(calculationHistoryRepository).findByCreatedAtBetween(startDate, endDate);
    }
    
    @Test
    void calculate_ShouldHandleRepositoryException_WhenSaveFails() {
        // Arrange
        when(externalPercentageService.getPercentage()).thenReturn(testPercentage);
        when(calculationHistoryRepository.save(any(CalculationHistory.class)))
            .thenThrow(new RuntimeException("Error de base de datos"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            calculationService.calculate(testRequest);
        });
        
        assertEquals("Error de base de datos", exception.getMessage());
        verify(externalPercentageService).getPercentage();
        verify(calculationHistoryRepository).save(any(CalculationHistory.class));
    }
    
    @Test
    void calculate_ShouldHandleExternalServiceException_WhenPercentageServiceFails() {
        // Arrange
        when(externalPercentageService.getPercentage())
            .thenThrow(new RuntimeException("Servicio externo no disponible"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            calculationService.calculate(testRequest);
        });
        
        assertEquals("Servicio externo no disponible", exception.getMessage());
        verify(externalPercentageService).getPercentage();
        verify(calculationHistoryRepository, never()).save(any(CalculationHistory.class));
    }

    private CalculationHistory createMockHistory(Long id, BigDecimal firstNumber, BigDecimal secondNumber, 
                                               BigDecimal percentage, BigDecimal result) {
        CalculationHistory history = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        history.setId(id);
        history.setCreatedAt(LocalDateTime.now());
        return history;
    }
}
