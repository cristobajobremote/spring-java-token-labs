package com.tokenlabs.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CalculationHistoryTest {
    
    private Validator validator;
    private CalculationHistory calculationHistory;
    private BigDecimal firstNumber;
    private BigDecimal secondNumber;
    private BigDecimal percentage;
    private BigDecimal result;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        firstNumber = new BigDecimal("10.50");
        secondNumber = new BigDecimal("20.25");
        percentage = new BigDecimal("15.75");
        result = new BigDecimal("35.59");
        
        calculationHistory = new CalculationHistory();
    }
    
    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenCalled() {
        // Act
        CalculationHistory history = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        
        // Assert
        assertNotNull(history);
        assertEquals(firstNumber, history.getFirstNumber());
        assertEquals(secondNumber, history.getSecondNumber());
        assertEquals(percentage, history.getPercentage());
        assertEquals(result, history.getResult());
        assertNotNull(history.getCreatedAt());
    }
    
    @Test
    void setId_ShouldSetValue_WhenCalled() {
        // Arrange
        Long id = 1L;
        
        // Act
        calculationHistory.setId(id);
        
        // Assert
        assertEquals(id, calculationHistory.getId());
    }
    
    @Test
    void setFirstNumber_ShouldSetValue_WhenCalled() {
        // Act
        calculationHistory.setFirstNumber(firstNumber);
        
        // Assert
        assertEquals(firstNumber, calculationHistory.getFirstNumber());
    }
    
    @Test
    void setSecondNumber_ShouldSetValue_WhenCalled() {
        // Act
        calculationHistory.setSecondNumber(secondNumber);
        
        // Assert
        assertEquals(secondNumber, calculationHistory.getSecondNumber());
    }
    
    @Test
    void setPercentage_ShouldSetValue_WhenCalled() {
        // Act
        calculationHistory.setPercentage(percentage);
        
        // Assert
        assertEquals(percentage, calculationHistory.getPercentage());
    }
    
    @Test
    void setResult_ShouldSetValue_WhenCalled() {
        // Act
        calculationHistory.setResult(result);
        
        // Assert
        assertEquals(result, calculationHistory.getResult());
    }
    
    @Test
    void setCreatedAt_ShouldSetValue_WhenCalled() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Act
        calculationHistory.setCreatedAt(timestamp);
        
        // Assert
        assertEquals(timestamp, calculationHistory.getCreatedAt());
    }
    
    @Test
    void getCreatedAt_ShouldReturnCurrentTime_WhenNotSet() {
        // Act
        LocalDateTime timestamp = calculationHistory.getCreatedAt();
        
        // Assert
        assertNotNull(timestamp);
        // Verificar que el timestamp está cerca del tiempo actual (dentro de 1 segundo)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(timestamp.isAfter(now.minusSeconds(1)));
        assertTrue(timestamp.isBefore(now.plusSeconds(1)));
    }
    
    @Test
    void constructor_ShouldSetCreatedAtAutomatically_WhenCalled() {
        // Act
        CalculationHistory history = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        
        // Assert
        assertNotNull(history.getCreatedAt());
        LocalDateTime now = LocalDateTime.now();
        assertTrue(history.getCreatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(history.getCreatedAt().isBefore(now.plusSeconds(1)));
    }
    
    @Test
    void validation_ShouldPass_WhenValidValuesProvided() {
        // Arrange
        calculationHistory.setFirstNumber(firstNumber);
        calculationHistory.setSecondNumber(secondNumber);
        calculationHistory.setPercentage(percentage);
        calculationHistory.setResult(result);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(calculationHistory);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validation_ShouldFail_WhenFirstNumberIsNull() {
        // Arrange
        CalculationHistory history = new CalculationHistory();
        history.setFirstNumber(null);
        history.setSecondNumber(secondNumber);
        history.setPercentage(percentage);
        history.setResult(result);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(history);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    void validation_ShouldFail_WhenSecondNumberIsNull() {
        // Arrange
        CalculationHistory history = new CalculationHistory();
        history.setFirstNumber(firstNumber);
        history.setSecondNumber(null);
        history.setPercentage(percentage);
        history.setResult(result);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(history);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    void validation_ShouldFail_WhenPercentageIsNull() {
        // Arrange
        CalculationHistory history = new CalculationHistory();
        history.setFirstNumber(firstNumber);
        history.setSecondNumber(secondNumber);
        history.setPercentage(null);
        history.setResult(result);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(history);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    void validation_ShouldFail_WhenResultIsNull() {
        // Arrange
        CalculationHistory history = new CalculationHistory();
        history.setFirstNumber(firstNumber);
        history.setSecondNumber(secondNumber);
        history.setPercentage(percentage);
        history.setResult(null);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(history);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    void validation_ShouldPass_WhenNegativeValuesProvided() {
        // Arrange
        calculationHistory.setFirstNumber(new BigDecimal("-10.0"));
        calculationHistory.setSecondNumber(new BigDecimal("-5.0"));
        calculationHistory.setPercentage(new BigDecimal("-20.0"));
        calculationHistory.setResult(new BigDecimal("-18.0"));
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(calculationHistory);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validation_ShouldPass_WhenZeroValuesProvided() {
        // Arrange
        calculationHistory.setFirstNumber(BigDecimal.ZERO);
        calculationHistory.setSecondNumber(BigDecimal.ZERO);
        calculationHistory.setPercentage(BigDecimal.ZERO);
        calculationHistory.setResult(BigDecimal.ZERO);
        
        // Act
        Set<ConstraintViolation<CalculationHistory>> violations = validator.validate(calculationHistory);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void toString_ShouldReturnStringRepresentation_WhenCalled() {
        // Arrange
        calculationHistory.setId(1L);
        calculationHistory.setFirstNumber(firstNumber);
        calculationHistory.setSecondNumber(secondNumber);
        calculationHistory.setPercentage(percentage);
        calculationHistory.setResult(result);
        
        // Act
        String resultString = calculationHistory.toString();
        
        // Assert
        assertNotNull(resultString);
        assertTrue(resultString.contains("1"));
        assertTrue(resultString.contains("10.50"));
        assertTrue(resultString.contains("20.25"));
        assertTrue(resultString.contains("15.75"));
        assertTrue(resultString.contains("35.59"));
    }
    
    @Test
    void equals_ShouldReturnTrue_WhenSameValues() {
        // Arrange
        CalculationHistory history1 = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        CalculationHistory history2 = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        history1.setId(1L);
        history2.setId(1L);
        
        // Act & Assert
        assertEquals(history1, history2);
    }
    
    @Test
    void equals_ShouldReturnFalse_WhenDifferentValues() {
        // Arrange
        CalculationHistory history1 = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        CalculationHistory history2 = new CalculationHistory(
            new BigDecimal("15.0"), 
            new BigDecimal("25.0"), 
            new BigDecimal("20.0"), 
            new BigDecimal("48.0")
        );
        
        // Act & Assert
        assertNotEquals(history1, history2);
    }
    
    @Test
    void hashCode_ShouldBeEqual_WhenSameValues() {
        // Arrange
        CalculationHistory history1 = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        CalculationHistory history2 = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        history1.setId(1L);
        history2.setId(1L);
        
        // Act & Assert
        assertEquals(history1.hashCode(), history2.hashCode());
    }
    
    @Test
    void shouldHandleLargeValues_WhenSet() {
        // Arrange
        BigDecimal largeValue = new BigDecimal("999999999.99");
        
        // Act
        calculationHistory.setFirstNumber(largeValue);
        calculationHistory.setSecondNumber(largeValue);
        calculationHistory.setPercentage(largeValue);
        calculationHistory.setResult(largeValue);
        
        // Assert
        assertEquals(largeValue, calculationHistory.getFirstNumber());
        assertEquals(largeValue, calculationHistory.getSecondNumber());
        assertEquals(largeValue, calculationHistory.getPercentage());
        assertEquals(largeValue, calculationHistory.getResult());
    }
    
    @Test
    void shouldHandleDecimalPrecision_WhenSet() {
        // Arrange
        BigDecimal decimalValue = new BigDecimal("123.456789");
        
        // Act
        calculationHistory.setFirstNumber(decimalValue);
        calculationHistory.setSecondNumber(decimalValue);
        calculationHistory.setPercentage(decimalValue);
        calculationHistory.setResult(decimalValue);
        
        // Assert
        assertEquals(decimalValue, calculationHistory.getFirstNumber());
        assertEquals(decimalValue, calculationHistory.getSecondNumber());
        assertEquals(decimalValue, calculationHistory.getPercentage());
        assertEquals(decimalValue, calculationHistory.getResult());
    }
}
