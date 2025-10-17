package com.tokenlabs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "first_number", precision = 10, scale = 2)
    private BigDecimal firstNumber;
    
    @NotNull
    @Column(name = "second_number", precision = 10, scale = 2)
    private BigDecimal secondNumber;
    
    @NotNull
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
    
    @NotNull
    @Column(name = "result", precision = 10, scale = 2)
    private BigDecimal result;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructores
    public CalculationHistory() {}
    
    public CalculationHistory(BigDecimal firstNumber, BigDecimal secondNumber, 
                             BigDecimal percentage, BigDecimal result) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.percentage = percentage;
        this.result = result;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
