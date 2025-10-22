package com.tokenlabs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExternalPercentageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalPercentageService.class);
    
    @Value("${app.external.percentage:15.75}")
    private BigDecimal configuredPercentage;
    
    private final CacheManager cacheManager;
    private final ExternalServiceFailureSimulator failureSimulator;
    
    // Fallback cache en memoria cuando Redis no esté disponible
    private final ConcurrentHashMap<String, BigDecimal> fallbackCache = new ConcurrentHashMap<>();
    
    public ExternalPercentageService(CacheManager cacheManager, ExternalServiceFailureSimulator failureSimulator) {
        this.cacheManager = cacheManager;
        this.failureSimulator = failureSimulator;
    }
    
    /**
     * Obtiene el porcentaje configurado desde el archivo YAML.
     * El resultado se cachea por 30 minutos.
     * Si el servicio externo falla, se usa el último valor almacenado en caché.
     * Si Redis no está disponible, usa un fallback cache en memoria.
     * Si el servicio externo falla Y no hay valor en caché, lanza excepción.
     * 
     * @return BigDecimal con el porcentaje configurado
     * @throws RuntimeException si el servicio externo falla y no hay valor en caché disponible
     */
    public BigDecimal getPercentage() {
        logger.info("Obteniendo porcentaje del servicio externo...");
        
        try {
            // Simular llamada al servicio externo
            BigDecimal percentage = callExternalService();
            logger.info("Porcentaje obtenido del servicio externo: {}%", percentage);
            
            // Intentar guardar en Redis cache, si falla usar fallback cache
            try {
                Cache cache = cacheManager.getCache("percentage");
                if (cache != null) {
                    cache.put("external-percentage", percentage);
                }
            } catch (Exception e) {
                logger.warn("Error al guardar en Redis cache, usando fallback cache: {}", e.getMessage());
            }
            
            // Siempre guardar en fallback cache como respaldo
            fallbackCache.put("external-percentage", percentage);
            
            return percentage;
            
        } catch (Exception e) {
            logger.error("Error al obtener porcentaje del servicio externo: {}", e.getMessage());
            
            // Intentar obtener el último valor del caché (Redis o fallback)
            BigDecimal cachedPercentage = getCachedPercentage();
            if (cachedPercentage != null) {
                logger.info("Usando último valor almacenado en caché: {}%", cachedPercentage);
                return cachedPercentage;
            }
            
            // Si no hay valor en caché y el servicio externo falla, lanzar excepción
            logger.error("Servicio externo falló y no hay valor en caché disponible");
            throw new RuntimeException("Servicio externo no disponible y no hay valor en caché para usar como fallback");
        }
    }
    
    /**
     * Simula la llamada al servicio externo.
     * Puede fallar para probar el manejo de errores.
     * 
     * @return BigDecimal con el porcentaje del servicio externo
     * @throws RuntimeException si el servicio externo falla
     */
    private BigDecimal callExternalService() {
        return failureSimulator.callExternalService();
    }
    
    /**
     * Obtiene el último valor almacenado en caché sin usar la anotación @Cacheable.
     * Si Redis no está disponible, usa el fallback cache en memoria.
     * 
     * @return BigDecimal con el valor en caché o null si no existe
     */
    private BigDecimal getCachedPercentage() {
        try {
            // Intentar usar Redis cache primero
            Cache cache = cacheManager.getCache("percentage");
            if (cache != null) {
                Cache.ValueWrapper valueWrapper = cache.get("external-percentage");
                if (valueWrapper != null) {
                    BigDecimal value = (BigDecimal) valueWrapper.get();
                    // Guardar también en fallback cache para futuras consultas
                    fallbackCache.put("external-percentage", value);
                    return value;
                }
            }
        } catch (Exception e) {
            logger.warn("Error al acceder al caché Redis, usando fallback cache: {}", e.getMessage());
        }
        
        // Si Redis no está disponible, usar fallback cache
        return fallbackCache.get("external-percentage");
    }
    
    /**
     * Método para limpiar el caché manualmente (útil para testing)
     */
    public void clearCache() {
        logger.info("Limpiando caché de porcentajes...");
        
        // Limpiar Redis cache
        try {
            Cache cache = cacheManager.getCache("percentage");
            if (cache != null) {
                cache.clear();
                logger.info("Redis caché limpiado exitosamente");
            }
        } catch (Exception e) {
            logger.warn("Error al limpiar Redis cache: {}", e.getMessage());
        }
        
        // Limpiar fallback cache
        fallbackCache.clear();
        logger.info("Fallback caché limpiado exitosamente");
    }
    
    /**
     * Método para simular fallo del servicio externo (útil para testing)
     */
    public void simulateExternalServiceFailure() {
        logger.warn("Simulando fallo del servicio externo...");
        // Este método se puede usar para testing
    }
}
