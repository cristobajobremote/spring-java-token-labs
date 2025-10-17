package com.tokenlabs.controller;

import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Calculation API", description = "API para cálculos con porcentajes externos")
public class CalculationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);
    
    @Autowired
    private CalculationService calculationService;
    
    @PostMapping("/calculate")
    @Operation(
        summary = "Realizar cálculo con porcentaje externo",
        description = "Suma dos números y aplica un porcentaje obtenido de un servicio externo con caché de 30 minutos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cálculo realizado exitosamente",
                    content = @Content(schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CalculationResponse> calculate(
            @Parameter(description = "Datos para el cálculo", required = true)
            @Valid @RequestBody CalculationRequest request) {
        
        logger.info("Recibida solicitud de cálculo: {}", request);
        
        try {
            CalculationResponse response = calculationService.calculate(request);
            logger.info("Cálculo completado exitosamente: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error durante el cálculo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/history")
    @Operation(
        summary = "Obtener historial de cálculos",
        description = "Retorna el historial completo de todos los cálculos realizados, ordenado por fecha descendente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = CalculationHistory.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<CalculationHistory>> getCalculationHistory() {
        logger.info("Solicitado historial de cálculos");
        
        try {
            List<CalculationHistory> history = calculationService.getCalculationHistory();
            logger.info("Historial obtenido: {} registros", history.size());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error al obtener historial", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/history/stats")
    @Operation(
        summary = "Obtener estadísticas de cálculos",
        description = "Retorna estadísticas básicas del historial de cálculos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<StatsResponse> getCalculationStats() {
        logger.info("Solicitadas estadísticas de cálculos");
        
        try {
            Long totalCalculations = calculationService.getTotalCalculations();
            StatsResponse stats = new StatsResponse(totalCalculations, LocalDateTime.now());
            logger.info("Estadísticas obtenidas: {} cálculos totales", totalCalculations);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/history/range")
    @Operation(
        summary = "Obtener historial por rango de fechas",
        description = "Retorna el historial de cálculos en un rango de fechas específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<CalculationHistory>> getCalculationHistoryByRange(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-ddTHH:mm:ss)", required = true)
            @RequestParam LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-ddTHH:mm:ss)", required = true)
            @RequestParam LocalDateTime endDate) {
        
        logger.info("Solicitado historial por rango: {} a {}", startDate, endDate);
        
        try {
            List<CalculationHistory> history = calculationService.getCalculationHistoryByDateRange(startDate, endDate);
            logger.info("Historial por rango obtenido: {} registros", history.size());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error al obtener historial por rango", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    @Operation(
        summary = "Verificar salud de la API",
        description = "Endpoint de salud para verificar que la API está funcionando correctamente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    })
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("OK", "API funcionando correctamente", LocalDateTime.now()));
    }
    
    // Clases internas para respuestas
    public static class StatsResponse {
        private Long totalCalculations;
        private LocalDateTime timestamp;
        
        public StatsResponse(Long totalCalculations, LocalDateTime timestamp) {
            this.totalCalculations = totalCalculations;
            this.timestamp = timestamp;
        }
        
        public Long getTotalCalculations() { return totalCalculations; }
        public void setTotalCalculations(Long totalCalculations) { this.totalCalculations = totalCalculations; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class HealthResponse {
        private String status;
        private String message;
        private LocalDateTime timestamp;
        
        public HealthResponse(String status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
