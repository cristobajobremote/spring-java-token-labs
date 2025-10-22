package com.tokenlabs.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheConfigTest {
    
    @Mock
    private RedisConnectionFactory redisConnectionFactory;
    
    @InjectMocks
    private CacheConfig cacheConfig;
    
    @Test
    void cacheManager_ShouldReturnCacheManager_WhenCalled() {
        // Act
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        // Assert
        assertNotNull(cacheManager);
        // Puede ser RedisCacheManager o ConcurrentMapCacheManager dependiendo de la disponibilidad de Redis
        assertTrue(cacheManager instanceof org.springframework.data.redis.cache.RedisCacheManager || 
                  cacheManager instanceof org.springframework.cache.concurrent.ConcurrentMapCacheManager);
    }
    
    @Test
    void cacheManager_ShouldCreateNewInstance_WhenCalledMultipleTimes() {
        // Act
        CacheManager cacheManager1 = cacheConfig.cacheManager();
        CacheManager cacheManager2 = cacheConfig.cacheManager();
        
        // Assert
        assertNotNull(cacheManager1);
        assertNotNull(cacheManager2);
        // Cada llamada debe crear una nueva instancia
        assertNotSame(cacheManager1, cacheManager2);
    }
    
    @Test
    void cacheManager_ShouldBeUsable_WhenCreated() {
        // Act
        CacheManager cacheManager = cacheConfig.cacheManager();
        
        // Assert
        assertNotNull(cacheManager);
        
        // Verificar que se puede obtener un caché
        org.springframework.cache.Cache cache = cacheManager.getCache("percentage");
        assertNotNull(cache);
        
        // Nota: No podemos probar put/get sin una conexión Redis real
        // Solo verificamos que el cacheManager se crea correctamente
    }
    
    @Test
    void fallbackCacheManager_ShouldReturnConcurrentMapCacheManager_WhenCalled() {
        // Act
        CacheManager fallbackCacheManager = cacheConfig.fallbackCacheManager();
        
        // Assert
        assertNotNull(fallbackCacheManager);
        assertTrue(fallbackCacheManager instanceof org.springframework.cache.concurrent.ConcurrentMapCacheManager);
    }
}
