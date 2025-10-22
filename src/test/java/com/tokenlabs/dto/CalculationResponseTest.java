package com.tokenlabs.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CalculationResponseTest {
    
    private CalculationResponse calculationResponse;
    private BigDecimal firstNumber;
    private BigDecimal secondNumber;
    private BigDecimal percentage;
    private BigDecimal result;
    
    @BeforeEach
    void setUp() {
        firstNumber = new BigDecimal("10.50");
        secondNumber = new BigDecimal("20.25");
        percentage = new BigDecimal("15.75");
        result = new BigDecimal("35.59");
        calculationResponse = new CalculationResponse();
    }
    
    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenCalled() {
        // Act
        CalculationResponse response = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        
        // Assert
        assertNotNull(response);
        assertEquals(firstNumber, response.getFirstNumber());
        assertEquals(secondNumber, response.getSecondNumber());
        assertEquals(percentage, response.getPercentage());
        assertEquals(result, response.getResult());
        assertNotNull(response.getTimestamp());
    }
    
    @Test
    void setFirstNumber_ShouldSetValue_WhenCalled() {
        // Act
        calculationResponse.setFirstNumber(firstNumber);
        
        // Assert
        assertEquals(firstNumber, calculationResponse.getFirstNumber());
    }
    
    @Test
    void setSecondNumber_ShouldSetValue_WhenCalled() {
        // Act
        calculationResponse.setSecondNumber(secondNumber);
        
        // Assert
        assertEquals(secondNumber, calculationResponse.getSecondNumber());
    }
    
    @Test
    void setPercentage_ShouldSetValue_WhenCalled() {
        // Act
        calculationResponse.setPercentage(percentage);
        
        // Assert
        assertEquals(percentage, calculationResponse.getPercentage());
    }
    
    @Test
    void setResult_ShouldSetValue_WhenCalled() {
        // Act
        calculationResponse.setResult(result);
        
        // Assert
        assertEquals(result, calculationResponse.getResult());
    }
    
    @Test
    void setTimestamp_ShouldSetValue_WhenCalled() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Act
        calculationResponse.setTimestamp(timestamp);
        
        // Assert
        assertEquals(timestamp, calculationResponse.getTimestamp());
    }
    
    @Test
    void getTimestamp_ShouldReturnCurrentTime_WhenNotSet() {
        // Act
        LocalDateTime timestamp = calculationResponse.getTimestamp();
        
        // Assert
        assertNotNull(timestamp);
        // Verificar que el timestamp está cerca del tiempo actual (dentro de 1 segundo)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(timestamp.isAfter(now.minusSeconds(1)));
        assertTrue(timestamp.isBefore(now.plusSeconds(1)));
    }
    
    @Test
    void constructor_ShouldSetTimestampAutomatically_WhenCalled() {
        // Act
        CalculationResponse response = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        
        // Assert
        assertNotNull(response.getTimestamp());
        LocalDateTime now = LocalDateTime.now();
        assertTrue(response.getTimestamp().isAfter(now.minusSeconds(1)));
        assertTrue(response.getTimestamp().isBefore(now.plusSeconds(1)));
    }
    
    @Test
    void toString_ShouldReturnStringRepresentation_WhenCalled() {
        // Arrange
        calculationResponse.setFirstNumber(firstNumber);
        calculationResponse.setSecondNumber(secondNumber);
        calculationResponse.setPercentage(percentage);
        calculationResponse.setResult(result);
        
        // Act
        String resultString = calculationResponse.toString();
        
        // Assert
        assertNotNull(resultString);
        assertTrue(resultString.contains("10.50"));
        assertTrue(resultString.contains("20.25"));
        assertTrue(resultString.contains("15.75"));
        assertTrue(resultString.contains("35.59"));
    }
    
    @Test
    void equals_ShouldReturnTrue_WhenSameValues() {
        // Arrange
        CalculationResponse response1 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        CalculationResponse response2 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        
        // Act & Assert
        assertEquals(response1, response2);
    }
    
    @Test
    void equals_ShouldReturnFalse_WhenDifferentValues() {
        // Arrange
        CalculationResponse response1 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        CalculationResponse response2 = new CalculationResponse(
            new BigDecimal("15.0"), 
            new BigDecimal("25.0"), 
            new BigDecimal("20.0"), 
            new BigDecimal("48.0")
        );
        
        // Act & Assert
        assertNotEquals(response1, response2);
    }
    
    @Test
    void hashCode_ShouldBeEqual_WhenSameValues() {
        // Arrange
        CalculationResponse response1 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        CalculationResponse response2 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        
        // Act & Assert
        assertEquals(response1.hashCode(), response2.hashCode());
    }
    
    @Test
    void hashCode_ShouldBeDifferent_WhenDifferentValues() {
        // Arrange
        CalculationResponse response1 = new CalculationResponse(firstNumber, secondNumber, percentage, result);
        CalculationResponse response2 = new CalculationResponse(
            new BigDecimal("15.0"), 
            new BigDecimal("25.0"), 
            new BigDecimal("20.0"), 
            new BigDecimal("48.0")
        );
        
        // Act & Assert
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }
    
    @Test
    void shouldHandleNullValues_WhenSet() {
        // Act
        calculationResponse.setFirstNumber(null);
        calculationResponse.setSecondNumber(null);
        calculationResponse.setPercentage(null);
        calculationResponse.setResult(null);
        
        // Assert
        assertNull(calculationResponse.getFirstNumber());
        assertNull(calculationResponse.getSecondNumber());
        assertNull(calculationResponse.getPercentage());
        assertNull(calculationResponse.getResult());
    }
    
    @Test
    void shouldHandleZeroValues_WhenSet() {
        // Arrange
        BigDecimal zero = BigDecimal.ZERO;
        
        // Act
        calculationResponse.setFirstNumber(zero);
        calculationResponse.setSecondNumber(zero);
        calculationResponse.setPercentage(zero);
        calculationResponse.setResult(zero);
        
        // Assert
        assertEquals(zero, calculationResponse.getFirstNumber());
        assertEquals(zero, calculationResponse.getSecondNumber());
        assertEquals(zero, calculationResponse.getPercentage());
        assertEquals(zero, calculationResponse.getResult());
    }
    
    @Test
    void shouldHandleNegativeValues_WhenSet() {
        // Arrange
        BigDecimal negative = new BigDecimal("-10.5");
        
        // Act
        calculationResponse.setFirstNumber(negative);
        calculationResponse.setSecondNumber(negative);
        calculationResponse.setPercentage(negative);
        calculationResponse.setResult(negative);
        
        // Assert
        assertEquals(negative, calculationResponse.getFirstNumber());
        assertEquals(negative, calculationResponse.getSecondNumber());
        assertEquals(negative, calculationResponse.getPercentage());
        assertEquals(negative, calculationResponse.getResult());
    }
}
