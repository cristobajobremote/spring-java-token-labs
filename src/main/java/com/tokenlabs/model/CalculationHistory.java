package com.tokenlabs.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @jakarta.validation.constraints.NotNull(message = "El primer número no puede ser nulo")
    @Column(name = "first_number", precision = 10, scale = 2)
    private BigDecimal firstNumber;
    
    @jakarta.validation.constraints.NotNull(message = "El segundo número no puede ser nulo")
    @Column(name = "second_number", precision = 10, scale = 2)
    private BigDecimal secondNumber;
    
    @jakarta.validation.constraints.NotNull(message = "El porcentaje no puede ser nulo")
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
    
    @jakarta.validation.constraints.NotNull(message = "El resultado no puede ser nulo")
    @Column(name = "result", precision = 10, scale = 2)
    private BigDecimal result;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructores
    public CalculationHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    public CalculationHistory(BigDecimal firstNumber, BigDecimal secondNumber, 
                             BigDecimal percentage, BigDecimal result) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.percentage = percentage;
        this.result = result;
        this.createdAt = LocalDateTime.now();
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
    
    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationHistory that = (CalculationHistory) o;
        return java.util.Objects.equals(id, that.id) &&
               java.util.Objects.equals(firstNumber, that.firstNumber) &&
               java.util.Objects.equals(secondNumber, that.secondNumber) &&
               java.util.Objects.equals(percentage, that.percentage) &&
               java.util.Objects.equals(result, that.result);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, firstNumber, secondNumber, percentage, result);
    }
    
    @Override
    public String toString() {
        return "CalculationHistory{" +
                "id=" + id +
                ", firstNumber=" + firstNumber +
                ", secondNumber=" + secondNumber +
                ", percentage=" + percentage +
                ", result=" + result +
                ", createdAt=" + createdAt +
                '}';
    }
}
