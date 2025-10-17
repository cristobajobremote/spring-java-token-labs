package com.tokenlabs.service;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class ExternalPercentageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalPercentageService.class);
    private final Random random = new Random();
    
    /**
     * Simula una llamada a un servicio externo para obtener un porcentaje.
     * El resultado se cachea por 30 minutos.
     * 
     * @return BigDecimal con el porcentaje obtenido del servicio externo
     */
    @Cacheable(value = "percentage", key = "'external-percentage'")
    public BigDecimal getPercentage() {
        logger.info("Llamando al servicio externo para obtener porcentaje...");
        
        // Simular latencia de red
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupción durante la simulación de latencia");
        }
        
        // Simular diferentes porcentajes basados en la hora del día
        BigDecimal percentage = generatePercentageByTime();
        
        logger.info("Porcentaje obtenido del servicio externo: {}%", percentage);
        return percentage;
    }
    
    /**
     * Genera un porcentaje basado en la hora actual para simular variaciones
     * del servicio externo a lo largo del día.
     */
    private BigDecimal generatePercentageByTime() {
        int hour = java.time.LocalTime.now().getHour();
        
        // Porcentajes diferentes según la hora del día
        if (hour >= 6 && hour < 12) {
            // Mañana: porcentajes más altos
            return BigDecimal.valueOf(15.0 + random.nextDouble() * 10.0); // 15-25%
        } else if (hour >= 12 && hour < 18) {
            // Tarde: porcentajes medios
            return BigDecimal.valueOf(10.0 + random.nextDouble() * 10.0); // 10-20%
        } else if (hour >= 18 && hour < 22) {
            // Noche: porcentajes variables
            return BigDecimal.valueOf(5.0 + random.nextDouble() * 15.0); // 5-20%
        } else {
            // Madrugada: porcentajes más bajos
            return BigDecimal.valueOf(2.0 + random.nextDouble() * 8.0); // 2-10%
        }
    }
    
    /**
     * Método para limpiar el caché manualmente (útil para testing)
     */
    public void clearCache() {
        logger.info("Limpiando caché de porcentajes...");
        // Este método se puede usar con @CacheEvict en otros servicios
    }
}
