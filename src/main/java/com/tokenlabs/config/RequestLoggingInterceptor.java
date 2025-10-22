package com.tokenlabs.config;

import com.tokenlabs.service.RequestHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    
    @Autowired
    private RequestHistoryService requestHistoryService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Almacenar el tiempo de inicio en el request
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            // Obtener información del request
            String endpoint = request.getRequestURI();
            
            // Excluir las consultas al endpoint /history para evitar recursión infinita
            if (endpoint.contains("/history")) {
                logger.debug("Skipping logging for history endpoint to avoid recursion: {}", endpoint);
                return;
            }
            
            long startTime = (Long) request.getAttribute("startTime");
            long executionTime = System.currentTimeMillis() - startTime;
            
            String httpMethod = request.getMethod();
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            // Obtener parámetros del request
            String requestParameters = getRequestParameters(request);
            String requestBody = getRequestBody(request);
            
            // Obtener información de la respuesta
            String responseStatus = String.valueOf(response.getStatus());
            String responseBody = getResponseBody(response);
            
            // Determinar si hay error
            String errorMessage = null;
            if (ex != null) {
                errorMessage = ex.getMessage();
            } else if (response.getStatus() >= 400) {
                errorMessage = "HTTP Error " + response.getStatus();
            }
            
            // Registrar de forma asíncrona
            requestHistoryService.logRequestAsync(
                    endpoint, httpMethod, requestParameters, requestBody,
                    responseStatus, responseBody, errorMessage, executionTime,
                    clientIp, userAgent
            );
            
        } catch (Exception e) {
            logger.error("Error in request logging interceptor: {}", e.getMessage());
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return null;
        }
        
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (params.length() > 0) {
                params.append("&");
            }
            params.append(entry.getKey()).append("=");
            if (entry.getValue().length > 0) {
                params.append(entry.getValue()[0]);
            }
        }
        
        return params.toString();
    }
    
    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return null;
    }
    
    private String getResponseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
