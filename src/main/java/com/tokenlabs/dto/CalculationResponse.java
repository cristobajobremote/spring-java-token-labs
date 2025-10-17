package com.tokenlabs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CalculationResponse {
    
    private BigDecimal firstNumber;
    private BigDecimal secondNumber;
    private BigDecimal percentage;
    private BigDecimal result;
    private LocalDateTime timestamp;
    
    // Constructores
    public CalculationResponse() {}
    
    public CalculationResponse(BigDecimal firstNumber, BigDecimal secondNumber, 
                              BigDecimal percentage, BigDecimal result) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.percentage = percentage;
        this.result = result;
        this.timestamp = LocalDateTime.now();
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
    
    public BigDecimal getPercentage() {
        return percentage;
    }
    
    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
    
    public BigDecimal getResult() {
        return result;
    }
    
    public void setResult(BigDecimal result) {
        this.result = result;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
