package com.tokenlabs.service;

import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.repository.CalculationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class CalculationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);
    
    @Autowired
    private ExternalPercentageService externalPercentageService;
    
    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;
    
    /**
     * Realiza el cálculo principal: suma dos números y aplica un porcentaje
     * obtenido del servicio externo.
     * 
     * @param request Request con los dos números a sumar
     * @return CalculationResponse con el resultado del cálculo
     */
    public CalculationResponse calculate(CalculationRequest request) {
        logger.info("Iniciando cálculo para números: {} y {}", 
                   request.getFirstNumber(), request.getSecondNumber());
        
        // Obtener porcentaje del servicio externo (con caché)
        BigDecimal percentage = externalPercentageService.getPercentage();
        
        // Realizar la suma
        BigDecimal sum = request.getFirstNumber().add(request.getSecondNumber());
        
        // Aplicar el porcentaje
        BigDecimal percentageAmount = sum.multiply(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal result = sum.add(percentageAmount);
        
        logger.info("Cálculo completado. Suma: {}, Porcentaje: {}%, Resultado: {}", 
                   sum, percentage, result);
        
        // Guardar en el historial
        CalculationHistory history = new CalculationHistory(
            request.getFirstNumber(),
            request.getSecondNumber(),
            percentage,
            result
        );
        calculationHistoryRepository.save(history);
        
        logger.info("Cálculo guardado en historial con ID: {}", history.getId());
        
        return new CalculationResponse(
            request.getFirstNumber(),
            request.getSecondNumber(),
            percentage,
            result
        );
    }
    
    /**
     * Obtiene el historial completo de cálculos ordenado por fecha descendente.
     * 
     * @return Lista de CalculationHistory
     */
    @Transactional(readOnly = true)
    public List<CalculationHistory> getCalculationHistory() {
        logger.info("Obteniendo historial completo de cálculos");
        return calculationHistoryRepository.findAllOrderByCreatedAtDesc();
    }
    
    /**
     * Obtiene estadísticas básicas del historial.
     * 
     * @return Número total de cálculos realizados
     */
    @Transactional(readOnly = true)
    public Long getTotalCalculations() {
        Long total = calculationHistoryRepository.countAllCalculations();
        logger.info("Total de cálculos en historial: {}", total);
        return total;
    }
    
    /**
     * Obtiene el historial de cálculos en un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de CalculationHistory en el rango especificado
     */
    @Transactional(readOnly = true)
    public List<CalculationHistory> getCalculationHistoryByDateRange(
            java.time.LocalDateTime startDate, 
            java.time.LocalDateTime endDate) {
        logger.info("Obteniendo historial de cálculos entre {} y {}", startDate, endDate);
        return calculationHistoryRepository.findByCreatedAtBetween(startDate, endDate);
    }
}
