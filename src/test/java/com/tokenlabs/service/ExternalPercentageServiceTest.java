package com.tokenlabs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExternalPercentageServiceTest {
    
    @InjectMocks
    private ExternalPercentageService externalPercentageService;
    
    @Test
    void getPercentage_ShouldReturnValidPercentage_WhenCalled() {
        // Act
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Assert
        assertNotNull(percentage);
        assertTrue(percentage.compareTo(BigDecimal.ZERO) > 0, "El porcentaje debe ser mayor que 0");
        assertTrue(percentage.compareTo(new BigDecimal("100")) <= 0, "El porcentaje debe ser menor o igual a 100");
    }
    
    @Test
    void getPercentage_ShouldSimulateNetworkLatency_WhenCalled() {
        // Arrange
        Instant start = Instant.now();
        
        // Act
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Assert
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        assertNotNull(percentage);
        // Verificar que la simulación de latencia funcionó (al menos 1 segundo)
        assertTrue(duration.toMillis() >= 1000, 
                  "La simulación de latencia debe tomar al menos 1 segundo");
    }
    
    @Test
    void clearCache_ShouldNotThrowException_WhenCalled() {
        // Act & Assert
        assertDoesNotThrow(() -> externalPercentageService.clearCache());
    }
}
