package com.tokenlabs.controller;

import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.dto.RequestHistoryFilter;
import com.tokenlabs.dto.RequestHistoryResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.model.RequestHistory;
import com.tokenlabs.service.CalculationService;
import com.tokenlabs.service.ExternalPercentageService;
import com.tokenlabs.service.ExternalServiceFailureSimulator;
import com.tokenlabs.service.RequestHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Calculation API", description = "API para cálculos con porcentajes externos")
public class CalculationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);
    
    @Autowired
    private CalculationService calculationService;
    
    @Autowired
    private ExternalPercentageService externalPercentageService;
    
    @Autowired
    private ExternalServiceFailureSimulator failureSimulator;
    
    @Autowired
    private RequestHistoryService requestHistoryService;
    
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
        
        CalculationResponse response = calculationService.calculate(request);
        logger.info("Cálculo completado exitosamente: {}", response);
        return ResponseEntity.ok(response);
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
    
    @PostMapping("/test/cache-clear")
    @Operation(
        summary = "Limpiar caché de porcentajes",
        description = "Endpoint para limpiar el caché de porcentajes (útil para testing)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Caché limpiado exitosamente")
    })
    public ResponseEntity<Map<String, Object>> clearCache() {
        logger.info("Solicitada limpieza de caché de porcentajes");
        
        try {
            externalPercentageService.clearCache();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Caché de porcentajes limpiado exitosamente");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Caché limpiado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al limpiar caché", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al limpiar caché: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/stats")
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
        
        Long totalCalculations = calculationService.getTotalCalculations();
        StatsResponse stats = new StatsResponse(totalCalculations, LocalDateTime.now());
        logger.info("Estadísticas obtenidas: {} cálculos totales", totalCalculations);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/test/simulate-failure")
    @Operation(
        summary = "Simular fallo del servicio externo",
        description = "Activa la simulación de fallo del servicio externo para probar el comportamiento del caché"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Simulación de fallo activada")
    })
    public ResponseEntity<Map<String, Object>> simulateFailure() {
        logger.info("Activando simulación de fallo del servicio externo");
        
        try {
            failureSimulator.enableFailureSimulation();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Simulación de fallo del servicio externo activada");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Simulación de fallo activada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al activar simulación de fallo", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al activar simulación: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/test/disable-failure")
    @Operation(
        summary = "Desactivar simulación de fallo",
        description = "Desactiva la simulación de fallo del servicio externo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Simulación de fallo desactivada")
    })
    public ResponseEntity<Map<String, Object>> disableFailure() {
        logger.info("Desactivando simulación de fallo del servicio externo");
        
        try {
            failureSimulator.disableFailureSimulation();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Simulación de fallo del servicio externo desactivada");
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Simulación de fallo desactivada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al desactivar simulación de fallo", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al desactivar simulación: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/test/failure-status")
    @Operation(
        summary = "Verificar estado de simulación de fallo",
        description = "Obtiene el estado actual de la simulación de fallo del servicio externo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente")
    })
    public ResponseEntity<Map<String, Object>> getFailureStatus() {
        logger.info("Consultando estado de simulación de fallo");
        
        try {
            boolean isActive = failureSimulator.isFailureSimulationActive();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("failureSimulationActive", isActive);
            response.put("message", isActive ? "Simulación de fallo activa" : "Simulación de fallo inactiva");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al consultar estado de simulación", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al consultar estado: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/history")
    @Operation(
        summary = "Obtener historial detallado de requests",
        description = "Obtiene el historial completo de requests con paginación, incluyendo fecha, endpoint, parámetros, respuesta/error"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> getRequestHistory(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "requestDate") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Filtrar por endpoint") @RequestParam(required = false) String endpoint,
            @Parameter(description = "Filtrar por método HTTP") @RequestParam(required = false) String httpMethod,
            @Parameter(description = "Filtrar por código de respuesta") @RequestParam(required = false) String responseStatus,
            @Parameter(description = "Filtrar por errores (true/false)") @RequestParam(required = false) Boolean hasError,
            @Parameter(description = "Fecha de inicio (ISO format)") @RequestParam(required = false) String startDate,
            @Parameter(description = "Fecha de fin (ISO format)") @RequestParam(required = false) String endDate) {
        
        logger.info("Consultando historial de requests - página: {}, tamaño: {}", page, size);
        
        try {
            // Validar parámetros
            if (page < 0) page = 0;
            if (size < 1) size = 20;
            if (size > 100) size = 100; // Límite máximo
            
            Page<RequestHistory> historyPage;
            
            // Si hay filtros, usar búsqueda con filtros
            if (endpoint != null || httpMethod != null || responseStatus != null || 
                hasError != null || startDate != null || endDate != null) {
                
                LocalDateTime start = null;
                LocalDateTime end = null;
                
                if (startDate != null) {
                    try {
                        start = LocalDateTime.parse(startDate);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("status", "error");
                        response.put("message", "Formato de fecha de inicio inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
                        return ResponseEntity.badRequest().body(response);
                    }
                }
                
                if (endDate != null) {
                    try {
                        end = LocalDateTime.parse(endDate);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("status", "error");
                        response.put("message", "Formato de fecha de fin inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
                        return ResponseEntity.badRequest().body(response);
                    }
                }
                
                historyPage = requestHistoryService.searchRequests(
                    endpoint, httpMethod, responseStatus, hasError,
                    start, end, page, size, sortBy, sortDirection
                );
            } else {
                // Sin filtros, obtener todo el historial
                historyPage = requestHistoryService.getRequestHistory(page, size, sortBy, sortDirection);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", historyPage.getContent());
            response.put("pagination", new HashMap<String, Object>() {{
                put("currentPage", historyPage.getNumber());
                put("totalPages", historyPage.getTotalPages());
                put("totalElements", historyPage.getTotalElements());
                put("size", historyPage.getSize());
                put("first", historyPage.isFirst());
                put("last", historyPage.isLast());
                put("numberOfElements", historyPage.getNumberOfElements());
            }});
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al consultar historial de requests", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al consultar historial: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/history/stats")
    @Operation(
        summary = "Obtener estadísticas del historial",
        description = "Obtiene estadísticas detalladas del historial de requests"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> getRequestHistoryStats() {
        logger.info("Consultando estadísticas del historial de requests");
        
        try {
            Object stats = requestHistoryService.getRequestStatistics();
            long totalRequests = requestHistoryService.getTotalRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("totalRequests", totalRequests);
            response.put("statistics", stats);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al consultar estadísticas del historial", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al consultar estadísticas: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/history/cleanup")
    @Operation(
        summary = "Limpiar registros antiguos",
        description = "Elimina registros de historial más antiguos de 30 días"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Limpieza iniciada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> cleanupOldRecords() {
        logger.info("Iniciando limpieza de registros antiguos");
        
        try {
            // La limpieza se ejecuta de forma asíncrona
            requestHistoryService.cleanOldRecords();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Limpieza de registros antiguos iniciada");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al iniciar limpieza de registros", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error al iniciar limpieza: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/calculations/history")
    @Operation(
        summary = "Obtener historial de cálculos",
        description = "Obtiene el historial paginado de todos los cálculos realizados con filtros opcionales"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial de cálculos obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> getCalculationsHistory(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (asc/desc)") @RequestParam(defaultValue = "desc") String sortDirection,
            @Parameter(description = "Fecha de inicio (ISO format)") @RequestParam(required = false) String startDate,
            @Parameter(description = "Fecha de fin (ISO format)") @RequestParam(required = false) String endDate) {
        
        logger.info("Consultando historial de cálculos - página: {}, tamaño: {}", page, size);
        
        try {
            // Validar parámetros de paginación
            if (page < 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "El número de página debe ser mayor o igual a 0");
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.badRequest().body(response);
            }
            
            if (size <= 0 || size > 100) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "El tamaño de página debe estar entre 1 y 100");
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Convertir fechas si se proporcionan
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    startDateTime = LocalDateTime.parse(startDate);
                } catch (Exception e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Formato de fecha de inicio inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
                    response.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    endDateTime = LocalDateTime.parse(endDate);
                } catch (Exception e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Formato de fecha de fin inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)");
                    response.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            // Obtener historial de cálculos
            Page<CalculationHistory> calculationHistoryPage = calculationService.getCalculationHistory(
                page, size, sortBy, sortDirection, startDateTime, endDateTime
            );
            
            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("timestamp", LocalDateTime.now());
            
            // Información de paginación
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", calculationHistoryPage.getNumber());
            pagination.put("totalPages", calculationHistoryPage.getTotalPages());
            pagination.put("totalElements", calculationHistoryPage.getTotalElements());
            pagination.put("size", calculationHistoryPage.getSize());
            pagination.put("numberOfElements", calculationHistoryPage.getNumberOfElements());
            pagination.put("first", calculationHistoryPage.isFirst());
            pagination.put("last", calculationHistoryPage.isLast());
            response.put("pagination", pagination);
            
            // Datos de cálculos
            List<Map<String, Object>> calculations = calculationHistoryPage.getContent().stream()
                .map(calc -> {
                    Map<String, Object> calcData = new HashMap<>();
                    calcData.put("id", calc.getId());
                    calcData.put("firstNumber", calc.getFirstNumber());
                    calcData.put("secondNumber", calc.getSecondNumber());
                    calcData.put("percentage", calc.getPercentage());
                    calcData.put("result", calc.getResult());
                    calcData.put("createdAt", calc.getCreatedAt());
                    return calcData;
                })
                .collect(Collectors.toList());
            
            response.put("data", calculations);
            
            logger.info("Historial de cálculos obtenido exitosamente - {} elementos", calculations.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener historial de cálculos", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error interno del servidor: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
