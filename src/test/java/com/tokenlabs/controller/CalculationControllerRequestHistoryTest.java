package com.tokenlabs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenlabs.model.RequestHistory;
import com.tokenlabs.service.RequestHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculationController.class)
class CalculationControllerRequestHistoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestHistoryService requestHistoryService;

    private RequestHistory testRequestHistory;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        testRequestHistory = new RequestHistory();
        testRequestHistory.setId(1L);
        testRequestHistory.setRequestDate(testDate);
        testRequestHistory.setEndpoint("/api/calculate");
        testRequestHistory.setHttpMethod("POST");
        testRequestHistory.setRequestParameters("firstNumber=10&secondNumber=20");
        testRequestHistory.setRequestBody("{\"firstNumber\": 10, \"secondNumber\": 20}");
        testRequestHistory.setResponseStatus("200");
        testRequestHistory.setResponseBody("{\"result\": 30.0}");
        testRequestHistory.setExecutionTimeMs(150L);
        testRequestHistory.setClientIp("192.168.1.1");
        testRequestHistory.setUserAgent("Mozilla/5.0");
    }

    @Test
    void getRequestHistory_ShouldReturnPaginatedHistory_WhenCalled() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc"))
                .thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "requestDate")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].endpoint").value("/api/calculate"))
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.totalElements").value(1))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(requestHistoryService).getRequestHistory(0, 20, "requestDate", "desc");
    }

    @Test
    void getRequestHistory_ShouldReturnFilteredHistory_WhenFiltersProvided() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.searchRequests(
                eq("/api/calculate"), eq("POST"), eq("200"), eq(false),
                any(LocalDateTime.class), any(LocalDateTime.class),
                eq(0), eq(20), eq("requestDate"), eq("desc")
        )).thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "20")
                .param("endpoint", "/api/calculate")
                .param("httpMethod", "POST")
                .param("responseStatus", "200")
                .param("hasError", "false")
                .param("startDate", testDate.minusDays(1).toString())
                .param("endDate", testDate.plusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].endpoint").value("/api/calculate"))
                .andExpect(jsonPath("$.pagination.currentPage").value(0));

        verify(requestHistoryService).searchRequests(
                eq("/api/calculate"), eq("POST"), eq("200"), eq(false),
                any(LocalDateTime.class), any(LocalDateTime.class),
                eq(0), eq(20), eq("requestDate"), eq("desc")
        );
    }

    @Test
    void getRequestHistory_ShouldReturnBadRequest_WhenInvalidDateFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "20")
                .param("startDate", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Formato de fecha de inicio inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)"));

        verify(requestHistoryService, never()).searchRequests(any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any());
    }

    @Test
    void getRequestHistory_ShouldReturnBadRequest_WhenInvalidEndDateFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "20")
                .param("endDate", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Formato de fecha de fin inválido. Use ISO format (yyyy-MM-ddTHH:mm:ss)"));

        verify(requestHistoryService, never()).searchRequests(any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any());
    }

    @Test
    void getRequestHistory_ShouldHandleServiceException_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc"))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al consultar historial: Database error"));

        verify(requestHistoryService).getRequestHistory(0, 20, "requestDate", "desc");
    }

    @Test
    void getRequestHistory_ShouldUseDefaultValues_WhenNoParametersProvided() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc"))
                .thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.size").value(1));

        verify(requestHistoryService).getRequestHistory(0, 20, "requestDate", "desc");
    }

    @Test
    void getRequestHistory_ShouldLimitMaxSize_WhenSizeExceedsLimit() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 100);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.getRequestHistory(0, 100, "requestDate", "desc"))
                .thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("size", "150") // Excede el límite de 100
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(requestHistoryService).getRequestHistory(0, 100, "requestDate", "desc");
    }

    @Test
    void getRequestHistoryStats_ShouldReturnStatistics_WhenCalled() throws Exception {
        // Arrange
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("requestsByEndpoint", Arrays.asList(new Object[]{"/api/calculate", 10L}));
        mockStats.put("requestsByHttpMethod", Arrays.asList(new Object[]{"POST", 8L}));
        mockStats.put("requestsByResponseStatus", Arrays.asList(new Object[]{"200", 9L}));
        mockStats.put("executionTimeStats", Arrays.asList(new Object[]{"/api/calculate", 150.0, 100L, 200L}));
        
        when(requestHistoryService.getRequestStatistics()).thenReturn(mockStats);
        when(requestHistoryService.getTotalRequests()).thenReturn(100L);

        // Act & Assert
        mockMvc.perform(get("/history/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.totalRequests").value(100))
                .andExpect(jsonPath("$.statistics").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(requestHistoryService).getRequestStatistics();
        verify(requestHistoryService).getTotalRequests();
    }

    @Test
    void getRequestHistoryStats_ShouldHandleServiceException_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(requestHistoryService.getRequestStatistics())
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/history/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al consultar estadísticas: Database error"));

        verify(requestHistoryService).getRequestStatistics();
    }

    @Test
    void cleanupOldRecords_ShouldReturnSuccess_WhenCalled() throws Exception {
        // Arrange
        when(requestHistoryService.cleanOldRecords())
                .thenReturn(CompletableFuture.completedFuture(5));

        // Act & Assert
        mockMvc.perform(post("/history/cleanup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Limpieza de registros antiguos iniciada"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(requestHistoryService).cleanOldRecords();
    }

    @Test
    void cleanupOldRecords_ShouldHandleServiceException_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(requestHistoryService.cleanOldRecords())
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/history/cleanup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error al iniciar limpieza: Database error"));

        verify(requestHistoryService).cleanOldRecords();
    }

    @Test
    void getRequestHistory_ShouldHandleNegativePage_WhenPageIsNegative() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc"))
                .thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "-1") // Página negativa
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(requestHistoryService).getRequestHistory(0, 20, "requestDate", "desc");
    }

    @Test
    void getRequestHistory_ShouldHandleZeroSize_WhenSizeIsZero() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc"))
                .thenReturn(expectedPage);

        // Act & Assert
        mockMvc.perform(get("/history")
                .param("page", "0")
                .param("size", "0") // Tamaño cero
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(requestHistoryService).getRequestHistory(0, 20, "requestDate", "desc");
    }
}
