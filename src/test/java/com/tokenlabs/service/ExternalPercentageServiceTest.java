package com.tokenlabs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalPercentageServiceTest {
    
    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private ExternalServiceFailureSimulator failureSimulator;
    
    @Mock
    private Cache cache;
    
    @InjectMocks
    private ExternalPercentageService externalPercentageService;
    
    private BigDecimal testPercentage;
    
    @BeforeEach
    void setUp() {
        testPercentage = new BigDecimal("15.75");
    }
    
    @Test
    void getPercentage_ShouldReturnValidPercentage_WhenCalled() {
        // Arrange
        when(failureSimulator.callExternalService()).thenReturn(testPercentage);
        
        // Act
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Assert
        assertNotNull(percentage);
        assertEquals(testPercentage, percentage);
        verify(failureSimulator).callExternalService();
    }
    
    @Test
    void getPercentage_ShouldReturnCachedValue_WhenExternalServiceFails() {
        // Arrange
        when(failureSimulator.callExternalService()).thenThrow(new RuntimeException("Servicio externo no disponible"));
        when(cacheManager.getCache("percentage")).thenReturn(cache);
        when(cache.get("external-percentage")).thenReturn(mock(Cache.ValueWrapper.class));
        when(cache.get("external-percentage").get()).thenReturn(testPercentage);
        
        // Act
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Assert
        assertNotNull(percentage);
        assertEquals(testPercentage, percentage);
        verify(failureSimulator).callExternalService();
        verify(cacheManager).getCache("percentage");
    }
    
    @Test
    void getPercentage_ShouldThrowException_WhenExternalServiceFailsAndNoCache() {
        // Arrange
        when(failureSimulator.callExternalService()).thenThrow(new RuntimeException("Servicio externo no disponible"));
        when(cacheManager.getCache("percentage")).thenReturn(cache);
        when(cache.get("external-percentage")).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalPercentageService.getPercentage();
        });
        
        assertEquals("Servicio externo no disponible y no hay valor en caché para usar como fallback", exception.getMessage());
        verify(failureSimulator).callExternalService();
        verify(cacheManager).getCache("percentage");
    }
    
    @Test
    void getPercentage_ShouldThrowException_WhenCacheManagerIsNull() {
        // Arrange
        when(failureSimulator.callExternalService()).thenThrow(new RuntimeException("Servicio externo no disponible"));
        when(cacheManager.getCache("percentage")).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalPercentageService.getPercentage();
        });
        
        assertEquals("Servicio externo no disponible y no hay valor en caché para usar como fallback", exception.getMessage());
        verify(failureSimulator).callExternalService();
        verify(cacheManager).getCache("percentage");
    }
    
    @Test
    void getPercentage_ShouldThrowException_WhenCacheThrowsException() {
        // Arrange
        when(failureSimulator.callExternalService()).thenThrow(new RuntimeException("Servicio externo no disponible"));
        when(cacheManager.getCache("percentage")).thenThrow(new RuntimeException("Error de caché"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalPercentageService.getPercentage();
        });
        
        assertEquals("Servicio externo no disponible y no hay valor en caché para usar como fallback", exception.getMessage());
        verify(failureSimulator).callExternalService();
        verify(cacheManager).getCache("percentage");
    }
    
    @Test
    void clearCache_ShouldClearCacheSuccessfully_WhenCalled() {
        // Arrange
        when(cacheManager.getCache("percentage")).thenReturn(cache);
        
        // Act
        externalPercentageService.clearCache();
        
        // Assert
        verify(cacheManager).getCache("percentage");
        verify(cache).clear();
    }
    
    @Test
    void clearCache_ShouldHandleNullCache_WhenCacheIsNull() {
        // Arrange
        when(cacheManager.getCache("percentage")).thenReturn(null);
        
        // Act & Assert
        assertDoesNotThrow(() -> externalPercentageService.clearCache());
        verify(cacheManager).getCache("percentage");
        verify(cache, never()).clear();
    }
    
    @Test
    void clearCache_ShouldHandleCacheException_WhenCacheThrowsException() {
        // Arrange
        when(cacheManager.getCache("percentage")).thenReturn(cache);
        doThrow(new RuntimeException("Error al limpiar caché")).when(cache).clear();
        
        // Act & Assert
        assertDoesNotThrow(() -> externalPercentageService.clearCache());
        verify(cacheManager).getCache("percentage");
        verify(cache).clear();
    }
    
    @Test
    void clearCache_ShouldHandleCacheManagerException_WhenCacheManagerThrowsException() {
        // Arrange
        when(cacheManager.getCache("percentage")).thenThrow(new RuntimeException("Error de cache manager"));
        
        // Act & Assert
        assertDoesNotThrow(() -> externalPercentageService.clearCache());
        verify(cacheManager).getCache("percentage");
    }
    
    @Test
    void simulateExternalServiceFailure_ShouldNotThrowException_WhenCalled() {
        // Act & Assert
        assertDoesNotThrow(() -> externalPercentageService.simulateExternalServiceFailure());
    }
    
    @Test
    void getPercentage_ShouldHandleInterruptedException_WhenThreadIsInterrupted() {
        // Arrange
        when(failureSimulator.callExternalService()).thenAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return testPercentage;
        });
        
        // Act
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Assert
        assertNotNull(percentage);
        assertEquals(testPercentage, percentage);
        verify(failureSimulator).callExternalService();
    }
}
