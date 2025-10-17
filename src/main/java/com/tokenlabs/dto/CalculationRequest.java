package com.tokenlabs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class CalculationRequest {
    
    @NotNull(message = "El primer número es obligatorio")
    @DecimalMin(value = "0.0", message = "El primer número debe ser mayor o igual a 0")
    private BigDecimal firstNumber;
    
    @NotNull(message = "El segundo número es obligatorio")
    @DecimalMin(value = "0.0", message = "El segundo número debe ser mayor o igual a 0")
    private BigDecimal secondNumber;
    
    // Constructores
    public CalculationRequest() {}
    
    public CalculationRequest(BigDecimal firstNumber, BigDecimal secondNumber) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
    }
    
    // Getters y Setters
    public BigDecimal getFirstNumber() {
        return firstNumber;
    }
    
    public void setFirstNumber(BigDecimal firstNumber) {
        this.firstNumber = firstNumber;
    }
    
    public BigDecimal getSecondNumber() {
        return secondNumber;
    }
    
    public void setSecondNumber(BigDecimal secondNumber) {
        this.secondNumber = secondNumber;
    }
}
