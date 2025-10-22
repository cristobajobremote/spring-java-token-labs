package com.tokenlabs.repository;

import com.tokenlabs.model.RequestHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RequestHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    private RequestHistory testRequestHistory1;
    private RequestHistory testRequestHistory2;
    private RequestHistory testRequestHistory3;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        
        testRequestHistory1 = new RequestHistory();
        testRequestHistory1.setRequestDate(testDate.minusHours(1));
        testRequestHistory1.setEndpoint("/api/calculate");
        testRequestHistory1.setHttpMethod("POST");
        testRequestHistory1.setRequestParameters("firstNumber=10&secondNumber=20");
        testRequestHistory1.setRequestBody("{\"firstNumber\": 10, \"secondNumber\": 20}");
        testRequestHistory1.setResponseStatus("200");
        testRequestHistory1.setResponseBody("{\"result\": 30.0}");
        testRequestHistory1.setExecutionTimeMs(150L);
        testRequestHistory1.setClientIp("192.168.1.1");
        testRequestHistory1.setUserAgent("Mozilla/5.0");

        testRequestHistory2 = new RequestHistory();
        testRequestHistory2.setRequestDate(testDate.minusHours(2));
        testRequestHistory2.setEndpoint("/api/history");
        testRequestHistory2.setHttpMethod("GET");
        testRequestHistory2.setRequestParameters("page=0&size=20");
        testRequestHistory2.setResponseStatus("200");
        testRequestHistory2.setResponseBody("{\"data\": []}");
        testRequestHistory2.setExecutionTimeMs(100L);
        testRequestHistory2.setClientIp("192.168.1.2");
        testRequestHistory2.setUserAgent("Chrome/91.0");

        testRequestHistory3 = new RequestHistory();
        testRequestHistory3.setRequestDate(testDate.minusHours(3));
        testRequestHistory3.setEndpoint("/api/calculate");
        testRequestHistory3.setHttpMethod("POST");
        testRequestHistory3.setRequestParameters("firstNumber=invalid");
        testRequestHistory3.setRequestBody("{\"firstNumber\": \"invalid\"}");
        testRequestHistory3.setResponseStatus("400");
        testRequestHistory3.setResponseBody("{\"error\": \"Invalid input\"}");
        testRequestHistory3.setErrorMessage("Validation failed");
        testRequestHistory3.setExecutionTimeMs(50L);
        testRequestHistory3.setClientIp("192.168.1.3");
        testRequestHistory3.setUserAgent("Safari/14.0");

        entityManager.persistAndFlush(testRequestHistory1);
        entityManager.persistAndFlush(testRequestHistory2);
        entityManager.persistAndFlush(testRequestHistory3);
    }

    @Test
    void findAllByOrderByRequestDateDesc_ShouldReturnAllRecordsOrderedByDate_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findAllByOrderByRequestDateDesc(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        
        // Verificar orden descendente por fecha
        List<RequestHistory> content = result.getContent();
        assertTrue(content.get(0).getRequestDate().isAfter(content.get(1).getRequestDate()));
        assertTrue(content.get(1).getRequestDate().isAfter(content.get(2).getRequestDate()));
    }

    @Test
    void findByEndpointContainingIgnoreCaseOrderByRequestDateDesc_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByEndpointContainingIgnoreCaseOrderByRequestDateDesc(
                "calculate", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        
        // Verificar que todos los resultados contienen "calculate" en el endpoint
        result.getContent().forEach(history -> 
            assertTrue(history.getEndpoint().toLowerCase().contains("calculate"))
        );
    }

    @Test
    void findByRequestDateBetweenOrderByRequestDateDesc_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        LocalDateTime startDate = testDate.minusHours(4);
        LocalDateTime endDate = testDate.minusHours(1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByRequestDateBetweenOrderByRequestDateDesc(
                startDate, endDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        
        // Verificar que todas las fechas están en el rango
        result.getContent().forEach(history -> {
            assertTrue(history.getRequestDate().isAfter(startDate) || history.getRequestDate().isEqual(startDate));
            assertTrue(history.getRequestDate().isBefore(endDate) || history.getRequestDate().isEqual(endDate));
        });
    }

    @Test
    void findByHttpMethodOrderByRequestDateDesc_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByHttpMethodOrderByRequestDateDesc("POST", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        
        // Verificar que todos los resultados tienen método POST
        result.getContent().forEach(history -> 
            assertEquals("POST", history.getHttpMethod())
        );
    }

    @Test
    void findByResponseStatusOrderByRequestDateDesc_ShouldReturnFilteredResults_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByResponseStatusOrderByRequestDateDesc("200", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        
        // Verificar que todos los resultados tienen status 200
        result.getContent().forEach(history -> 
            assertEquals("200", history.getResponseStatus())
        );
    }

    @Test
    void findByErrorMessageIsNotNullOrderByRequestDateDesc_ShouldReturnErrorRequests_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByErrorMessageIsNotNullOrderByRequestDateDesc(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        // Verificar que el resultado tiene error message
        assertNotNull(result.getContent().get(0).getErrorMessage());
    }

    @Test
    void findByErrorMessageIsNullOrderByRequestDateDesc_ShouldReturnSuccessfulRequests_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findByErrorMessageIsNullOrderByRequestDateDesc(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        
        // Verificar que todos los resultados no tienen error message
        result.getContent().forEach(history -> 
            assertNull(history.getErrorMessage())
        );
    }

    @Test
    void countRequestsByEndpoint_ShouldReturnEndpointCounts_WhenCalled() {
        // Act
        List<Object[]> result = requestHistoryRepository.countRequestsByEndpoint();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Dos endpoints únicos
        
        // Verificar que los resultados contienen los endpoints esperados
        boolean foundCalculate = false;
        boolean foundHistory = false;
        
        for (Object[] row : result) {
            String endpoint = (String) row[0];
            Long count = (Long) row[1];
            
            if ("/api/calculate".equals(endpoint)) {
                assertEquals(2L, count);
                foundCalculate = true;
            } else if ("/api/history".equals(endpoint)) {
                assertEquals(1L, count);
                foundHistory = true;
            }
        }
        
        assertTrue(foundCalculate);
        assertTrue(foundHistory);
    }

    @Test
    void countRequestsByHttpMethod_ShouldReturnMethodCounts_WhenCalled() {
        // Act
        List<Object[]> result = requestHistoryRepository.countRequestsByHttpMethod();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Dos métodos únicos
        
        // Verificar que los resultados contienen los métodos esperados
        boolean foundPost = false;
        boolean foundGet = false;
        
        for (Object[] row : result) {
            String method = (String) row[0];
            Long count = (Long) row[1];
            
            if ("POST".equals(method)) {
                assertEquals(2L, count);
                foundPost = true;
            } else if ("GET".equals(method)) {
                assertEquals(1L, count);
                foundGet = true;
            }
        }
        
        assertTrue(foundPost);
        assertTrue(foundGet);
    }

    @Test
    void countRequestsByResponseStatus_ShouldReturnStatusCounts_WhenCalled() {
        // Act
        List<Object[]> result = requestHistoryRepository.countRequestsByResponseStatus();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Dos status únicos
        
        // Verificar que los resultados contienen los status esperados
        boolean found200 = false;
        boolean found400 = false;
        
        for (Object[] row : result) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            
            if ("200".equals(status)) {
                assertEquals(2L, count);
                found200 = true;
            } else if ("400".equals(status)) {
                assertEquals(1L, count);
                found400 = true;
            }
        }
        
        assertTrue(found200);
        assertTrue(found400);
    }

    @Test
    void findWithFilters_ShouldReturnFilteredResults_WhenMultipleFiltersApplied() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findWithFilters(
                "/api/calculate", "POST", "200", false,
                testDate.minusHours(4), testDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        RequestHistory history = result.getContent().get(0);
        assertEquals("/api/calculate", history.getEndpoint());
        assertEquals("POST", history.getHttpMethod());
        assertEquals("200", history.getResponseStatus());
        assertNull(history.getErrorMessage());
    }

    @Test
    void findWithFilters_ShouldReturnAllResults_WhenNoFiltersApplied() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));

        // Act
        Page<RequestHistory> result = requestHistoryRepository.findWithFilters(
                null, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    @Test
    void deleteOldRecords_ShouldDeleteRecordsOlderThanCutoffDate_WhenCalled() {
        // Arrange
        LocalDateTime cutoffDate = testDate.minusHours(2);

        // Act
        int deletedCount = requestHistoryRepository.deleteOldRecords(cutoffDate);

        // Assert
        assertEquals(1, deletedCount);
        
        // Verificar que el registro más antiguo fue eliminado
        Pageable pageable = PageRequest.of(0, 10);
        Page<RequestHistory> remainingRecords = requestHistoryRepository.findAllByOrderByRequestDateDesc(pageable);
        assertEquals(2, remainingRecords.getTotalElements());
    }
}
