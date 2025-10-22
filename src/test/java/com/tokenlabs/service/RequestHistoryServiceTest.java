package com.tokenlabs.service;

import com.tokenlabs.model.RequestHistory;
import com.tokenlabs.repository.RequestHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestHistoryServiceTest {

    @Mock
    private RequestHistoryRepository requestHistoryRepository;

    @InjectMocks
    private RequestHistoryService requestHistoryService;

    private RequestHistory testRequestHistory;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        testRequestHistory = new RequestHistory();
        testRequestHistory.setId(1L);
        testRequestHistory.setRequestDate(testDate);
        testRequestHistory.setEndpoint("/api/test");
        testRequestHistory.setHttpMethod("GET");
        testRequestHistory.setRequestParameters("param=value");
        testRequestHistory.setRequestBody("{\"test\": \"data\"}");
        testRequestHistory.setResponseStatus("200");
        testRequestHistory.setResponseBody("{\"result\": \"success\"}");
        testRequestHistory.setExecutionTimeMs(150L);
        testRequestHistory.setClientIp("192.168.1.1");
        testRequestHistory.setUserAgent("Mozilla/5.0");
    }

    @Test
    void logRequestAsync_ShouldSaveRequestHistory_WhenCalled() {
        // Arrange
        when(requestHistoryRepository.save(any(RequestHistory.class))).thenReturn(testRequestHistory);

        // Act
        CompletableFuture<Void> result = requestHistoryService.logRequestAsync(
                "/api/test", "GET", "param=value", "{\"test\": \"data\"}",
                "200", "{\"result\": \"success\"}", null, 150L,
                "192.168.1.1", "Mozilla/5.0"
        );

        // Assert
        assertNotNull(result);
        verify(requestHistoryRepository, timeout(1000)).save(any(RequestHistory.class));
    }

    @Test
    void logRequestAsync_ShouldHandleErrorGracefully_WhenRepositoryThrowsException() {
        // Arrange
        when(requestHistoryRepository.save(any(RequestHistory.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> result = requestHistoryService.logRequestAsync(
                    "/api/test", "GET", "param=value", "{\"test\": \"data\"}",
                    "200", "{\"result\": \"success\"}", null, 150L,
                    "192.168.1.1", "Mozilla/5.0"
            );
            assertNotNull(result);
        });
    }

    @Test
    void getRequestHistory_ShouldReturnPaginatedResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findAllByOrderByRequestDateDesc(pageable)).thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.getRequestHistory(0, 20, "requestDate", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testRequestHistory.getEndpoint(), result.getContent().get(0).getEndpoint());
        verify(requestHistoryRepository).findAllByOrderByRequestDateDesc(pageable);
    }

    @Test
    void searchRequests_ShouldReturnFilteredResults_WhenFiltersProvided() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findWithFilters(
                eq("/api/test"), eq("GET"), eq("200"), eq(false),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)
        )).thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.searchRequests(
                "/api/test", "GET", "200", false,
                testDate.minusDays(1), testDate.plusDays(1),
                0, 20, "requestDate", "desc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findWithFilters(
                eq("/api/test"), eq("GET"), eq("200"), eq(false),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)
        );
    }

    @Test
    void searchRequests_ShouldHandleNullFilters_WhenSomeFiltersAreNull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findWithFilters(
                isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), eq(pageable)
        )).thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.searchRequests(
                null, null, null, null,
                null, null,
                0, 20, "requestDate", "desc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findWithFilters(
                isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), eq(pageable)
        );
    }

    @Test
    void getRequestStatistics_ShouldReturnStatistics_WhenCalled() {
        // Arrange
        List<Object[]> endpointStats = new ArrayList<>();
        endpointStats.add(new Object[]{"/api/test", 10L});
        
        List<Object[]> methodStats = new ArrayList<>();
        methodStats.add(new Object[]{"GET", 8L});
        methodStats.add(new Object[]{"POST", 2L});
        
        List<Object[]> statusStats = new ArrayList<>();
        statusStats.add(new Object[]{"200", 9L});
        statusStats.add(new Object[]{"400", 1L});
        
        List<Object[]> timeStats = new ArrayList<>();
        timeStats.add(new Object[]{"/api/test", 150.0, 100L, 200L});
        
        when(requestHistoryRepository.countRequestsByEndpoint()).thenReturn(endpointStats);
        when(requestHistoryRepository.countRequestsByHttpMethod()).thenReturn(methodStats);
        when(requestHistoryRepository.countRequestsByResponseStatus()).thenReturn(statusStats);
        when(requestHistoryRepository.getExecutionTimeStatsByEndpoint()).thenReturn(timeStats);

        // Act
        Object result = requestHistoryService.getRequestStatistics();

        // Assert
        assertNotNull(result);
        verify(requestHistoryRepository).countRequestsByEndpoint();
        verify(requestHistoryRepository).countRequestsByHttpMethod();
        verify(requestHistoryRepository).countRequestsByResponseStatus();
        verify(requestHistoryRepository).getExecutionTimeStatsByEndpoint();
    }

    @Test
    void cleanOldRecords_ShouldReturnDeletedCount_WhenCalled() {
        // Arrange
        int expectedDeletedCount = 5;
        when(requestHistoryRepository.deleteOldRecords(any(LocalDateTime.class)))
                .thenReturn(expectedDeletedCount);

        // Act
        CompletableFuture<Integer> result = requestHistoryService.cleanOldRecords();

        // Assert
        assertNotNull(result);
        verify(requestHistoryRepository, timeout(1000)).deleteOldRecords(any(LocalDateTime.class));
    }

    @Test
    void cleanOldRecords_ShouldHandleException_WhenRepositoryThrowsException() {
        // Arrange
        when(requestHistoryRepository.deleteOldRecords(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        CompletableFuture<Integer> result = requestHistoryService.cleanOldRecords();

        // Assert
        assertNotNull(result);
        verify(requestHistoryRepository, timeout(1000)).deleteOldRecords(any(LocalDateTime.class));
    }

    @Test
    void getTotalRequests_ShouldReturnCount_WhenCalled() {
        // Arrange
        long expectedCount = 100L;
        when(requestHistoryRepository.count()).thenReturn(expectedCount);

        // Act
        long result = requestHistoryService.getTotalRequests();

        // Assert
        assertEquals(expectedCount, result);
        verify(requestHistoryRepository).count();
    }

    @Test
    void getRequestsByEndpoint_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findByEndpointContainingIgnoreCaseOrderByRequestDateDesc(
                "/api/test", pageable
        )).thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.getRequestsByEndpoint("/api/test", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findByEndpointContainingIgnoreCaseOrderByRequestDateDesc(
                "/api/test", pageable
        );
    }

    @Test
    void getRequestsByHttpMethod_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findByHttpMethodOrderByRequestDateDesc("GET", pageable))
                .thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.getRequestsByHttpMethod("GET", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findByHttpMethodOrderByRequestDateDesc("GET", pageable);
    }

    @Test
    void getRequestsWithErrors_ShouldReturnErrorRequests_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findByErrorMessageIsNotNullOrderByRequestDateDesc(pageable))
                .thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.getRequestsWithErrors(0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findByErrorMessageIsNotNullOrderByRequestDateDesc(pageable);
    }

    @Test
    void getSuccessfulRequests_ShouldReturnSuccessfulRequests_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<RequestHistory> expectedPage = new PageImpl<>(Arrays.asList(testRequestHistory), pageable, 1);
        
        when(requestHistoryRepository.findByErrorMessageIsNullOrderByRequestDateDesc(pageable))
                .thenReturn(expectedPage);

        // Act
        Page<RequestHistory> result = requestHistoryService.getSuccessfulRequests(0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(requestHistoryRepository).findByErrorMessageIsNullOrderByRequestDateDesc(pageable);
    }
}
