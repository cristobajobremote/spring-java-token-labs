package com.tokenlabs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenlabs.dto.CalculationRequest;
import com.tokenlabs.dto.CalculationResponse;
import com.tokenlabs.model.CalculationHistory;
import com.tokenlabs.service.CalculationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CalculationService calculationService;
    
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
    
    private CalculationHistory createMockHistory(Long id, BigDecimal firstNumber, BigDecimal secondNumber, 
                                               BigDecimal percentage, BigDecimal result) {
        CalculationHistory history = new CalculationHistory(firstNumber, secondNumber, percentage, result);
        history.setId(id);
        history.setCreatedAt(LocalDateTime.now());
        return history;
    }
}
