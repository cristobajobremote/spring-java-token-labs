package com.tokenlabs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExternalServiceFailureSimulatorTest {
    
    @InjectMocks
    private ExternalServiceFailureSimulator failureSimulator;
    
    private BigDecimal testPercentage;
    
    @BeforeEach
    void setUp() {
        testPercentage = new BigDecimal("15.75");
    }
    
    @Test
    void callExternalService_ShouldReturnPercentage_WhenFailureSimulationIsDisabled() {
        // Arrange
        failureSimulator.disableFailureSimulation();
        
        // Act
        BigDecimal result = failureSimulator.callExternalService();
        
        // Assert
        assertNotNull(result);
        assertEquals(testPercentage, result);
        assertFalse(failureSimulator.isFailureSimulationActive());
    }
    
    @Test
    void callExternalService_ShouldThrowException_WhenFailureSimulationIsEnabled() {
        // Arrange
        failureSimulator.enableFailureSimulation();
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            failureSimulator.callExternalService();
        });
        
        assertEquals("Servicio externo temporalmente no disponible (simulado)", exception.getMessage());
        assertTrue(failureSimulator.isFailureSimulationActive());
    }
    
    @Test
    void enableFailureSimulation_ShouldSetSimulationToTrue_WhenCalled() {
        // Act
        failureSimulator.enableFailureSimulation();
        
        // Assert
        assertTrue(failureSimulator.isFailureSimulationActive());
    }
    
    @Test
    void disableFailureSimulation_ShouldSetSimulationToFalse_WhenCalled() {
        // Arrange
        failureSimulator.enableFailureSimulation();
        assertTrue(failureSimulator.isFailureSimulationActive());
        
        // Act
        failureSimulator.disableFailureSimulation();
        
        // Assert
        assertFalse(failureSimulator.isFailureSimulationActive());
    }
    
    @Test
    void isFailureSimulationActive_ShouldReturnFalse_WhenInitiallyCreated() {
        // Act
        boolean isActive = failureSimulator.isFailureSimulationActive();
        
        // Assert
        assertFalse(isActive);
    }
    
    @Test
    void callExternalService_ShouldSimulateNetworkLatency_WhenCalled() {
        // Arrange
        failureSimulator.disableFailureSimulation();
        
        // Act
        Instant start = Instant.now();
        BigDecimal result = failureSimulator.callExternalService();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        // Assert
        assertNotNull(result);
        assertEquals(testPercentage, result);
        // Verificar que la simulación de latencia funcionó (al menos 100ms)
        assertTrue(duration.toMillis() >= 100, 
                  "La simulación de latencia debe tomar al menos 100ms");
    }
    
    @Test
    void callExternalService_ShouldHandleInterruptedException_WhenThreadIsInterrupted() {
        // Arrange
        failureSimulator.disableFailureSimulation();
        
        // Act
        Thread.currentThread().interrupt();
        BigDecimal result = failureSimulator.callExternalService();
        
        // Assert
        assertNotNull(result);
        assertEquals(testPercentage, result);
        // Verificar que el flag de interrupción fue restaurado
        assertTrue(Thread.currentThread().isInterrupted());
        
        // Limpiar el flag de interrupción para otros tests
        Thread.interrupted();
    }
    
    @Test
    void callExternalService_ShouldHandleMultipleInterruptions_WhenCalledMultipleTimes() {
        // Arrange
        failureSimulator.disableFailureSimulation();
        
        // Act & Assert
        for (int i = 0; i < 3; i++) {
            BigDecimal result = failureSimulator.callExternalService();
            assertNotNull(result);
            assertEquals(testPercentage, result);
        }
    }
    
    @Test
    void enableFailureSimulation_ShouldBeIdempotent_WhenCalledMultipleTimes() {
        // Act
        failureSimulator.enableFailureSimulation();
        failureSimulator.enableFailureSimulation();
        failureSimulator.enableFailureSimulation();
        
        // Assert
        assertTrue(failureSimulator.isFailureSimulationActive());
        
        // Verificar que sigue fallando
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            failureSimulator.callExternalService();
        });
        assertEquals("Servicio externo temporalmente no disponible (simulado)", exception.getMessage());
    }
    
    @Test
    void disableFailureSimulation_ShouldBeIdempotent_WhenCalledMultipleTimes() {
        // Arrange
        failureSimulator.enableFailureSimulation();
        assertTrue(failureSimulator.isFailureSimulationActive());
        
        // Act
        failureSimulator.disableFailureSimulation();
        failureSimulator.disableFailureSimulation();
        failureSimulator.disableFailureSimulation();
        
        // Assert
        assertFalse(failureSimulator.isFailureSimulationActive());
        
        // Verificar que funciona correctamente
        BigDecimal result = failureSimulator.callExternalService();
        assertNotNull(result);
        assertEquals(testPercentage, result);
    }
    
    @Test
    void toggleFailureSimulation_ShouldWorkCorrectly_WhenSwitchingStates() {
        // Test initial state
        assertFalse(failureSimulator.isFailureSimulationActive());
        BigDecimal result1 = failureSimulator.callExternalService();
        assertNotNull(result1);
        
        // Enable failure simulation
        failureSimulator.enableFailureSimulation();
        assertTrue(failureSimulator.isFailureSimulationActive());
        assertThrows(RuntimeException.class, () -> failureSimulator.callExternalService());
        
        // Disable failure simulation
        failureSimulator.disableFailureSimulation();
        assertFalse(failureSimulator.isFailureSimulationActive());
        BigDecimal result2 = failureSimulator.callExternalService();
        assertNotNull(result2);
        
        // Verify both results are the same
        assertEquals(result1, result2);
    }
}
