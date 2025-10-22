package com.tokenlabs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.service.CalculationService;
import com.tokenlabs.service.ExternalPercentageService;
import com.tokenlabs.service.ExternalServiceFailureSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CalculationService calculationService;
    
    @MockBean
    private ExternalPercentageService externalPercentageService;
    
    @MockBean
    private ExternalServiceFailureSimulator failureSimulator;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private CalculationRequest testRequest;
    private CalculationResponse testResponse;
    
    @BeforeEach
    void setUp() {
        testRequest = new CalculationRequest(
            new BigDecimal("10.50"),
            new BigDecimal("20.25")
        );
        
        testResponse = new CalculationResponse(
            new BigDecimal("10.50"),
            new BigDecimal("20.25"),
            new BigDecimal("15.75"),
            new BigDecimal("35.59")
        );
    }
    
    @Test
    void calculate_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Arrange
        when(calculationService.calculate(any(CalculationRequest.class))).thenReturn(testResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstNumber").value(10.50))
                .andExpect(jsonPath("$.secondNumber").value(20.25))
                .andExpect(jsonPath("$.percentage").value(15.75))
                .andExpect(jsonPath("$.result").value(35.59))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void calculate_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        CalculationRequest invalidRequest = new CalculationRequest();
        invalidRequest.setFirstNumber(new BigDecimal("-5.0")); // Valor negativo
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void calculate_ShouldReturnBadRequest_WhenNullValues() throws Exception {
        // Arrange
        CalculationRequest nullRequest = new CalculationRequest();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getCalculationHistory_ShouldReturnOk_WhenHistoryExists() throws Exception {
        // Arrange
        List<CalculationHistory> mockHistory = Arrays.asList(
            createMockHistory(1L, new BigDecimal("5.0"), new BigDecimal("10.0"), 
                            new BigDecimal("12.5"), new BigDecimal("16.88")),
            createMockHistory(2L, new BigDecimal("3.0"), new BigDecimal("7.0"), 
                            new BigDecimal("8.0"), new BigDecimal("10.80"))
        );
        
        when(calculationService.getCalculationHistory()).thenReturn(mockHistory);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstNumber").value(5.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstNumber").value(3.0));
    }
    
    @Test
    void getCalculationStats_ShouldReturnOk_WhenCalled() throws Exception {
        // Arrange
        when(calculationService.getTotalCalculations()).thenReturn(5L);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/history/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCalculations").value(5))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void getCalculationHistoryByRange_ShouldReturnOk_WhenValidRange() throws Exception {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<CalculationHistory> mockHistory = Arrays.asList(
            createMockHistory(1L, new BigDecimal("1.0"), new BigDecimal("2.0"), 
                            new BigDecimal("10.0"), new BigDecimal("3.30"))
        );
        
        when(calculationService.getCalculationHistoryByDateRange(startDate, endDate))
            .thenReturn(mockHistory);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/history/range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }
    
    @Test
    void health_ShouldReturnOk_WhenCalled() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("API funcionando correctamente"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void calculate_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(calculationService.calculate(any(CalculationRequest.class)))
            .thenThrow(new RuntimeException("Error interno"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isInternalServerError());
    }
    
    // ========== TESTS PARA ENDPOINTS DE TESTING ==========
    
    @Test
    void clearCache_ShouldReturnOk_WhenCalled() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/cache-clear"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Caché de porcentajes limpiado exitosamente"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void simulateFailure_ShouldReturnOk_WhenCalled() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/simulate-failure"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Simulación de fallo del servicio externo activada"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void disableFailure_ShouldReturnOk_WhenCalled() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/disable-failure"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Simulación de fallo del servicio externo desactivada"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void getFailureStatus_ShouldReturnOk_WhenCalled() throws Exception {
        // Arrange
        when(failureSimulator.isFailureSimulationActive()).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/test/failure-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.failureSimulationActive").value(false))
                .andExpect(jsonPath("$.message").value("Simulación de fallo inactiva"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void getFailureStatus_ShouldReturnActiveStatus_WhenSimulationIsActive() throws Exception {
        // Arrange
        when(failureSimulator.isFailureSimulationActive()).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/test/failure-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.failureSimulationActive").value(true))
                .andExpect(jsonPath("$.message").value("Simulación de fallo activa"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void clearCache_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Error al limpiar caché")).when(externalPercentageService).clearCache();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/cache-clear"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al limpiar caché: Error al limpiar caché"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void simulateFailure_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Error al activar simulación")).when(failureSimulator).enableFailureSimulation();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/simulate-failure"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al activar simulación: Error al activar simulación"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void disableFailure_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Error al desactivar simulación")).when(failureSimulator).disableFailureSimulation();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/test/disable-failure"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al desactivar simulación: Error al desactivar simulación"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void getFailureStatus_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(failureSimulator.isFailureSimulationActive()).thenThrow(new RuntimeException("Error al consultar estado"));
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/test/failure-status"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al consultar estado: Error al consultar estado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private CalculationHistory createMockHistory(Long id, BigDecimal firstNumber, BigDecimal secondNumber, 
                                               BigDecimal percentage, BigDecimal result) {
        CalculationHistory history = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        history.setId(id);
        history.setCreatedAt(LocalDateTime.now());
        return history;
    }
}
