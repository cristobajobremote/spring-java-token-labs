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
    public CalculationResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
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
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationResponse that = (CalculationResponse) o;
        return java.util.Objects.equals(firstNumber, that.firstNumber) &&
               java.util.Objects.equals(secondNumber, that.secondNumber) &&
               java.util.Objects.equals(percentage, that.percentage) &&
               java.util.Objects.equals(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(firstNumber, secondNumber, percentage, result);
    }
    
    @Override
    public String toString() {
        return "CalculationResponse{" +
                "firstNumber=" + firstNumber +
                ", secondNumber=" + secondNumber +
                ", percentage=" + percentage +
                ", result=" + result +
                ", timestamp=" + timestamp +
                '}';
    }
}
