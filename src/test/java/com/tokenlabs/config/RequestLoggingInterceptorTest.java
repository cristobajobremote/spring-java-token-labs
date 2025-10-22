package com.tokenlabs.config;

import com.tokenlabs.service.RequestHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingInterceptorTest {

    @Mock
    private RequestHistoryService requestHistoryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private RequestLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        when(requestHistoryService.logRequestAsync(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void preHandle_ShouldSetStartTime_WhenCalled() throws Exception {
        // Act
        boolean result = interceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(request).setAttribute("startTime", anyLong());
    }

    @Test
    void afterCompletion_ShouldLogRequest_WhenCalledSuccessfully() throws Exception {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(200);

        // Act
        interceptor.afterCompletion(request, response, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("GET"), isNull(), isNull(),
                eq("200"), isNull(), isNull(), anyLong(),
                eq("192.168.1.1"), eq("Mozilla/5.0")
        );
    }

    @Test
    void afterCompletion_ShouldLogRequestWithError_WhenExceptionOccurs() throws Exception {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Chrome/91.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(500);

        RuntimeException exception = new RuntimeException("Test error");

        // Act
        interceptor.afterCompletion(request, response, null, exception);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("POST"), isNull(), isNull(),
                eq("500"), isNull(), eq("Test error"), anyLong(),
                eq("192.168.1.1"), eq("Chrome/91.0")
        );
    }

    @Test
    void afterCompletion_ShouldLogRequestWithHttpError_WhenResponseStatusIsError() throws Exception {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Safari/14.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(400);

        // Act
        interceptor.afterCompletion(request, response, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("GET"), isNull(), isNull(),
                eq("400"), isNull(), eq("HTTP Error 400"), anyLong(),
                eq("192.168.1.1"), eq("Safari/14.0")
        );
    }

    @Test
    void afterCompletion_ShouldHandleServiceException_WhenLoggingServiceThrowsException() throws Exception {
        // Arrange
        when(requestHistoryService.logRequestAsync(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Logging service error"));

        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(200);

        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> {
            interceptor.afterCompletion(request, response, null, null);
        });

        verify(requestHistoryService).logRequestAsync(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    void afterCompletion_ShouldExtractClientIpFromXForwardedFor_WhenHeaderPresent() throws Exception {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18, 150.172.238.178");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(200);

        // Act
        interceptor.afterCompletion(request, response, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("GET"), isNull(), isNull(),
                eq("200"), isNull(), isNull(), anyLong(),
                eq("203.0.113.195"), eq("Mozilla/5.0")
        );
    }

    @Test
    void afterCompletion_ShouldExtractClientIpFromXRealIp_WhenHeaderPresent() throws Exception {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Real-IP")).thenReturn("203.0.113.195");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(response.getStatus()).thenReturn(200);

        // Act
        interceptor.afterCompletion(request, response, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("GET"), isNull(), isNull(),
                eq("200"), isNull(), isNull(), anyLong(),
                eq("203.0.113.195"), eq("Mozilla/5.0")
        );
    }

    @Test
    void afterCompletion_ShouldHandleContentCachingRequestWrapper_WhenRequestIsWrapped() throws Exception {
        // Arrange
        ContentCachingRequestWrapper wrappedRequest = mock(ContentCachingRequestWrapper.class);
        when(wrappedRequest.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(wrappedRequest.getRequestURI()).thenReturn("/api/test");
        when(wrappedRequest.getMethod()).thenReturn("POST");
        when(wrappedRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(wrappedRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(wrappedRequest.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(wrappedRequest.getContentAsByteArray()).thenReturn("{\"test\": \"data\"}".getBytes());
        when(response.getStatus()).thenReturn(200);

        // Act
        interceptor.afterCompletion(wrappedRequest, response, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("POST"), isNull(), eq("{\"test\": \"data\"}"),
                eq("200"), isNull(), isNull(), anyLong(),
                eq("192.168.1.1"), eq("Mozilla/5.0")
        );
    }

    @Test
    void afterCompletion_ShouldHandleContentCachingResponseWrapper_WhenResponseIsWrapped() throws Exception {
        // Arrange
        ContentCachingResponseWrapper wrappedResponse = mock(ContentCachingResponseWrapper.class);
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getParameterMap()).thenReturn(java.util.Collections.emptyMap());
        when(wrappedResponse.getStatus()).thenReturn(200);
        when(wrappedResponse.getContentAsByteArray()).thenReturn("{\"result\": \"success\"}".getBytes());

        // Act
        interceptor.afterCompletion(request, wrappedResponse, null, null);

        // Assert
        verify(requestHistoryService, timeout(1000)).logRequestAsync(
                eq("/api/test"), eq("GET"), isNull(), isNull(),
                eq("200"), eq("{\"result\": \"success\"}"), isNull(), anyLong(),
                eq("192.168.1.1"), eq("Mozilla/5.0")
        );
    }
}
