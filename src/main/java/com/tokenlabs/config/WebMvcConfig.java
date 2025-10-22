package com.tokenlabs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**") // Interceptar todos los endpoints
                .excludePathPatterns(
                    "/actuator/**",     // Excluir endpoints de Actuator
                    "/swagger-ui/**",   // Excluir Swagger UI
                    "/v3/api-docs/**",  // Excluir documentación de API
                    "/error",           // Excluir página de error
                    "/api/v1/history/**" // Excluir endpoint de historial para evitar recursión
                );
    }
}
