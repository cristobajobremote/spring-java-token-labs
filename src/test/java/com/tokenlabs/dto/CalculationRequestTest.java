package com.tokenlabs.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CalculationRequestTest {
    
    private Validator validator;
    private CalculationRequest calculationRequest;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        calculationRequest = new CalculationRequest();
    }
    
    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenCalled() {
        // Arrange
        BigDecimal firstNumber = new BigDecimal("10.50");
        BigDecimal secondNumber = new BigDecimal("20.25");
        
        // Act
        CalculationRequest request = new CalculationRequest(firstNumber, secondNumber);
        
        // Assert
        assertNotNull(request);
        assertEquals(firstNumber, request.getFirstNumber());
        assertEquals(secondNumber, request.getSecondNumber());
    }
    
    @Test
    void setFirstNumber_ShouldSetValue_WhenCalled() {
        // Arrange
        BigDecimal firstNumber = new BigDecimal("15.75");
        
        // Act
        calculationRequest.setFirstNumber(firstNumber);
        
        // Assert
        assertEquals(firstNumber, calculationRequest.getFirstNumber());
    }
    
    @Test
    void setSecondNumber_ShouldSetValue_WhenCalled() {
        // Arrange
        BigDecimal secondNumber = new BigDecimal("25.50");
        
        // Act
        calculationRequest.setSecondNumber(secondNumber);
        
        // Assert
        assertEquals(secondNumber, calculationRequest.getSecondNumber());
    }
    
    @Test
    void validation_ShouldPass_WhenValidValuesProvided() {
        // Arrange
        calculationRequest.setFirstNumber(new BigDecimal("10.0"));
        calculationRequest.setSecondNumber(new BigDecimal("20.0"));
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(calculationRequest);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validation_ShouldFail_WhenFirstNumberIsNull() {
        // Arrange
        CalculationRequest request = new CalculationRequest();
        request.setFirstNumber(null);
        request.setSecondNumber(new BigDecimal("20.0"));
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(request);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("obligatorio"));
    }
    
    @Test
    void validation_ShouldFail_WhenSecondNumberIsNull() {
        // Arrange
        CalculationRequest request = new CalculationRequest();
        request.setFirstNumber(new BigDecimal("10.0"));
        request.setSecondNumber(null);
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(request);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("obligatorio"));
    }
    
    @Test
    void validation_ShouldFail_WhenBothNumbersAreNull() {
        // Arrange
        calculationRequest.setFirstNumber(null);
        calculationRequest.setSecondNumber(null);
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(calculationRequest);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }
    
    @Test
    void validation_ShouldFail_WhenNegativeValuesProvided() {
        // Arrange
        CalculationRequest request = new CalculationRequest();
        request.setFirstNumber(new BigDecimal("-10.0"));
        request.setSecondNumber(new BigDecimal("-5.0"));
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(request);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // Ambos números violan @DecimalMin
    }
    
    @Test
    void validation_ShouldPass_WhenZeroValuesProvided() {
        // Arrange
        calculationRequest.setFirstNumber(BigDecimal.ZERO);
        calculationRequest.setSecondNumber(BigDecimal.ZERO);
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(calculationRequest);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validation_ShouldPass_WhenLargeValuesProvided() {
        // Arrange
        calculationRequest.setFirstNumber(new BigDecimal("999999999.99"));
        calculationRequest.setSecondNumber(new BigDecimal("888888888.88"));
        
        // Act
        Set<ConstraintViolation<CalculationRequest>> violations = validator.validate(calculationRequest);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void toString_ShouldReturnStringRepresentation_WhenCalled() {
        // Arrange
        calculationRequest.setFirstNumber(new BigDecimal("10.50"));
        calculationRequest.setSecondNumber(new BigDecimal("20.25"));
        
        // Act
        String result = calculationRequest.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("10.50"));
        assertTrue(result.contains("20.25"));
    }
    
    @Test
    void equals_ShouldReturnTrue_WhenSameValues() {
        // Arrange
        CalculationRequest request1 = new CalculationRequest(new BigDecimal("10.0"), new BigDecimal("20.0"));
        CalculationRequest request2 = new CalculationRequest(new BigDecimal("10.0"), new BigDecimal("20.0"));
        
        // Act & Assert
        assertEquals(request1, request2);
    }
    
    @Test
    void equals_ShouldReturnFalse_WhenDifferentValues() {
        // Arrange
        CalculationRequest request1 = new CalculationRequest(new BigDecimal("10.0"), new BigDecimal("20.0"));
        CalculationRequest request2 = new CalculationRequest(new BigDecimal("15.0"), new BigDecimal("25.0"));
        
        // Act & Assert
        assertNotEquals(request1, request2);
    }
    
    @Test
    void hashCode_ShouldBeEqual_WhenSameValues() {
        // Arrange
        CalculationRequest request1 = new CalculationRequest(new BigDecimal("10.0"), new BigDecimal("20.0"));
        CalculationRequest request2 = new CalculationRequest(new BigDecimal("10.0"), new BigDecimal("20.0"));
        
        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
