package com.tokenlabs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Service
public class ExternalServiceFailureSimulator {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceFailureSimulator.class);
    
    @Value("${app.external.percentage:15.75}")
    private BigDecimal configuredPercentage;
    
    private boolean simulateFailure = false;
    
    /**
     * Simula la llamada al servicio externo con posibilidad de fallo.
     * 
     * @return BigDecimal con el porcentaje del servicio externo
     * @throws RuntimeException si el servicio externo falla
     */
    public BigDecimal callExternalService() {
        if (simulateFailure) {
            logger.error("Simulando fallo del servicio externo");
            throw new RuntimeException("Servicio externo temporalmente no disponible (simulado)");
        }
        
        // Simular latencia de red
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupción durante la simulación de latencia");
        }
        
        logger.info("Servicio externo respondió correctamente con porcentaje: {}%", configuredPercentage);
        return configuredPercentage != null ? configuredPercentage : new BigDecimal("15.75");
    }
    
    /**
     * Activa la simulación de fallo del servicio externo.
     */
    public void enableFailureSimulation() {
        this.simulateFailure = true;
        logger.warn("Simulación de fallo del servicio externo ACTIVADA");
    }
    
    /**
     * Desactiva la simulación de fallo del servicio externo.
     */
    public void disableFailureSimulation() {
        this.simulateFailure = false;
        logger.info("Simulación de fallo del servicio externo DESACTIVADA");
    }
    
    /**
     * Verifica si la simulación de fallo está activa.
     * 
     * @return true si la simulación está activa, false en caso contrario
     */
    public boolean isFailureSimulationActive() {
        return simulateFailure;
    }
}
