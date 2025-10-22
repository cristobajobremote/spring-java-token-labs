package com.tokenlabs.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RequestHistoryFilterTest {

    private RequestHistoryFilter filter;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        filter = new RequestHistoryFilter();
        filter.setEndpoint("/api/test");
        filter.setHttpMethod("GET");
        filter.setResponseStatus("200");
        filter.setHasError(false);
        filter.setStartDate(testDate.minusDays(1));
        filter.setEndDate(testDate.plusDays(1));
        filter.setPage(0);
        filter.setSize(20);
        filter.setSortBy("requestDate");
        filter.setSortDirection("desc");
    }

    @Test
    void constructor_ShouldCreateInstanceWithDefaultValues_WhenDefaultConstructorCalled() {
        // Arrange
        RequestHistoryFilter newFilter = new RequestHistoryFilter();

        // Assert
        assertNotNull(newFilter);
        assertNull(newFilter.getEndpoint());
        assertNull(newFilter.getHttpMethod());
        assertNull(newFilter.getResponseStatus());
        assertNull(newFilter.getHasError());
        assertNull(newFilter.getStartDate());
        assertNull(newFilter.getEndDate());
        assertEquals(0, newFilter.getPage());
        assertEquals(20, newFilter.getSize());
        assertEquals("requestDate", newFilter.getSortBy());
        assertEquals("desc", newFilter.getSortDirection());
    }

    @Test
    void constructor_ShouldCreateInstanceWithValues_WhenParameterizedConstructorCalled() {
        // Arrange
        String endpoint = "/api/calculate";
        String httpMethod = "POST";
        String responseStatus = "200";
        Boolean hasError = false;
        LocalDateTime startDate = testDate.minusDays(1);
        LocalDateTime endDate = testDate.plusDays(1);
        Integer page = 1;
        Integer size = 10;
        String sortBy = "endpoint";
        String sortDirection = "asc";

        // Act
        RequestHistoryFilter newFilter = new RequestHistoryFilter(
                endpoint, httpMethod, responseStatus, hasError,
                startDate, endDate, page, size, sortBy, sortDirection
        );

        // Assert
        assertNotNull(newFilter);
        assertEquals(endpoint, newFilter.getEndpoint());
        assertEquals(httpMethod, newFilter.getHttpMethod());
        assertEquals(responseStatus, newFilter.getResponseStatus());
        assertEquals(hasError, newFilter.getHasError());
        assertEquals(startDate, newFilter.getStartDate());
        assertEquals(endDate, newFilter.getEndDate());
        assertEquals(page, newFilter.getPage());
        assertEquals(size, newFilter.getSize());
        assertEquals(sortBy, newFilter.getSortBy());
        assertEquals(sortDirection, newFilter.getSortDirection());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String newEndpoint = "/api/new";
        String newHttpMethod = "PUT";
        String newResponseStatus = "201";
        Boolean newHasError = true;
        LocalDateTime newStartDate = testDate.minusDays(2);
        LocalDateTime newEndDate = testDate.plusDays(2);
        Integer newPage = 2;
        Integer newSize = 50;
        String newSortBy = "endpoint";
        String newSortDirection = "asc";

        // Act
        filter.setEndpoint(newEndpoint);
        filter.setHttpMethod(newHttpMethod);
        filter.setResponseStatus(newResponseStatus);
        filter.setHasError(newHasError);
        filter.setStartDate(newStartDate);
        filter.setEndDate(newEndDate);
        filter.setPage(newPage);
        filter.setSize(newSize);
        filter.setSortBy(newSortBy);
        filter.setSortDirection(newSortDirection);

        // Assert
        assertEquals(newEndpoint, filter.getEndpoint());
        assertEquals(newHttpMethod, filter.getHttpMethod());
        assertEquals(newResponseStatus, filter.getResponseStatus());
        assertEquals(newHasError, filter.getHasError());
        assertEquals(newStartDate, filter.getStartDate());
        assertEquals(newEndDate, filter.getEndDate());
        assertEquals(newPage, filter.getPage());
        assertEquals(newSize, filter.getSize());
        assertEquals(newSortBy, filter.getSortBy());
        assertEquals(newSortDirection, filter.getSortDirection());
    }

    @Test
    void equals_ShouldReturnTrue_WhenObjectsAreIdentical() {
        // Arrange
        RequestHistoryFilter filter1 = new RequestHistoryFilter();
        filter1.setEndpoint("/api/test");
        filter1.setHttpMethod("GET");
        filter1.setResponseStatus("200");
        filter1.setHasError(false);
        filter1.setPage(0);
        filter1.setSize(20);

        RequestHistoryFilter filter2 = new RequestHistoryFilter();
        filter2.setEndpoint("/api/test");
        filter2.setHttpMethod("GET");
        filter2.setResponseStatus("200");
        filter2.setHasError(false);
        filter2.setPage(0);
        filter2.setSize(20);

        // Assert
        assertTrue(filter1.equals(filter2));
        assertTrue(filter2.equals(filter1));
        assertEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenObjectsAreDifferent() {
        // Arrange
        RequestHistoryFilter filter1 = new RequestHistoryFilter();
        filter1.setEndpoint("/api/test");
        filter1.setHttpMethod("GET");
        filter1.setResponseStatus("200");
        filter1.setHasError(false);
        filter1.setPage(0);
        filter1.setSize(20);

        RequestHistoryFilter filter2 = new RequestHistoryFilter();
        filter2.setEndpoint("/api/different");
        filter2.setHttpMethod("GET");
        filter2.setResponseStatus("200");
        filter2.setHasError(false);
        filter2.setPage(0);
        filter2.setSize(20);

        // Assert
        assertFalse(filter1.equals(filter2));
        assertFalse(filter2.equals(filter1));
        assertNotEquals(filter1.hashCode(), filter2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedWithNull() {
        // Arrange
        RequestHistoryFilter filter = new RequestHistoryFilter();

        // Assert
        assertFalse(filter.equals(null));
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedWithDifferentClass() {
        // Arrange
        RequestHistoryFilter filter = new RequestHistoryFilter();
        String differentObject = "not a RequestHistoryFilter";

        // Assert
        assertFalse(filter.equals(differentObject));
    }

    @Test
    void toString_ShouldReturnExpectedFormat() {
        // Arrange
        String expectedString = "RequestHistoryFilter{" +
                "endpoint='/api/test', httpMethod='GET', responseStatus='200', hasError=false, " +
                "startDate=" + testDate.minusDays(1) + ", endDate=" + testDate.plusDays(1) + ", " +
                "page=0, size=20, sortBy='requestDate', sortDirection='desc'}";
        String actualString = filter.toString();

        // Assert
        assertEquals(expectedString, actualString);
    }

    @Test
    void hashCode_ShouldReturnSameValue_WhenCalledMultipleTimes() {
        // Act
        int hashCode1 = filter.hashCode();
        int hashCode2 = filter.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_ShouldReturnDifferentValues_WhenObjectsAreDifferent() {
        // Arrange
        RequestHistoryFilter filter1 = new RequestHistoryFilter();
        filter1.setEndpoint("/api/test");
        filter1.setHttpMethod("GET");
        filter1.setResponseStatus("200");
        filter1.setHasError(false);
        filter1.setPage(0);
        filter1.setSize(20);

        RequestHistoryFilter filter2 = new RequestHistoryFilter();
        filter2.setEndpoint("/api/different");
        filter2.setHttpMethod("GET");
        filter2.setResponseStatus("200");
        filter2.setHasError(false);
        filter2.setPage(0);
        filter2.setSize(20);

        // Act
        int hashCode1 = filter1.hashCode();
        int hashCode2 = filter2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void allFields_ShouldBeNullable_WhenNotSet() {
        // Arrange
        RequestHistoryFilter newFilter = new RequestHistoryFilter();

        // Assert
        assertNull(newFilter.getEndpoint());
        assertNull(newFilter.getHttpMethod());
        assertNull(newFilter.getResponseStatus());
        assertNull(newFilter.getHasError());
        assertNull(newFilter.getStartDate());
        assertNull(newFilter.getEndDate());
        // Los campos page, size, sortBy, sortDirection tienen valores por defecto
        assertEquals(0, newFilter.getPage());
        assertEquals(20, newFilter.getSize());
        assertEquals("requestDate", newFilter.getSortBy());
        assertEquals("desc", newFilter.getSortDirection());
    }

    @Test
    void allFields_ShouldAcceptNullValues_WhenSetToNull() {
        // Act
        filter.setEndpoint(null);
        filter.setHttpMethod(null);
        filter.setResponseStatus(null);
        filter.setHasError(null);
        filter.setStartDate(null);
        filter.setEndDate(null);
        filter.setPage(null);
        filter.setSize(null);
        filter.setSortBy(null);
        filter.setSortDirection(null);

        // Assert
        assertNull(filter.getEndpoint());
        assertNull(filter.getHttpMethod());
        assertNull(filter.getResponseStatus());
        assertNull(filter.getHasError());
        assertNull(filter.getStartDate());
        assertNull(filter.getEndDate());
        assertNull(filter.getPage());
        assertNull(filter.getSize());
        assertNull(filter.getSortBy());
        assertNull(filter.getSortDirection());
    }

    @Test
    void defaultValues_ShouldBeCorrect_WhenCreatedWithDefaultConstructor() {
        // Arrange
        RequestHistoryFilter newFilter = new RequestHistoryFilter();

        // Assert
        assertEquals(0, newFilter.getPage());
        assertEquals(20, newFilter.getSize());
        assertEquals("requestDate", newFilter.getSortBy());
        assertEquals("desc", newFilter.getSortDirection());
    }

    @Test
    void booleanFields_ShouldHandleTrueAndFalseValues() {
        // Arrange
        RequestHistoryFilter filter1 = new RequestHistoryFilter();
        RequestHistoryFilter filter2 = new RequestHistoryFilter();

        // Act
        filter1.setHasError(true);
        filter2.setHasError(false);

        // Assert
        assertTrue(filter1.getHasError());
        assertFalse(filter2.getHasError());
        assertNotEquals(filter1, filter2);
    }

    @Test
    void dateFields_ShouldHandleDifferentDates() {
        // Arrange
        RequestHistoryFilter filter1 = new RequestHistoryFilter();
        RequestHistoryFilter filter2 = new RequestHistoryFilter();
        LocalDateTime date1 = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2025, 1, 2, 10, 0);

        // Act
        filter1.setStartDate(date1);
        filter1.setEndDate(date2);
        filter2.setStartDate(date2);
        filter2.setEndDate(date1);

        // Assert
        assertEquals(date1, filter1.getStartDate());
        assertEquals(date2, filter1.getEndDate());
        assertEquals(date2, filter2.getStartDate());
        assertEquals(date1, filter2.getEndDate());
        assertNotEquals(filter1, filter2);
    }
}
