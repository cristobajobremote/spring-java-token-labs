package com.tokenlabs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    
    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;
    
    @Bean
    @Primary
    public CacheManager cacheManager() {
        if (redisConnectionFactory != null) {
            try {
                // Intentar crear Redis cache manager
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30)) // TTL de 30 minutos
                        .serializeKeysWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                        .disableCachingNullValues();
                
                CacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                        .cacheDefaults(config)
                        .build();
                
                logger.info("Redis CacheManager configurado exitosamente");
                return redisCacheManager;
                
            } catch (Exception e) {
                logger.warn("Error al configurar Redis CacheManager, usando fallback en memoria: {}", e.getMessage());
            }
        } else {
            logger.warn("RedisConnectionFactory no disponible, usando fallback en memoria");
        }
        
        return fallbackCacheManager();
    }
    
    @Bean("fallbackCacheManager")
    public CacheManager fallbackCacheManager() {
        logger.info("Configurando CacheManager de fallback en memoria");
        return new ConcurrentMapCacheManager("percentage");
    }
}
