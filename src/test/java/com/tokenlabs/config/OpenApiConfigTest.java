package com.tokenlabs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {
    
    @InjectMocks
    private OpenApiConfig openApiConfig;
    
    @Test
    void customOpenAPI_ShouldReturnOpenAPIWithCorrectInfo_WhenCalled() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        
        Info info = openAPI.getInfo();
        assertEquals("Spring Java Token Labs API", info.getTitle());
        assertEquals("API REST para cálculos con porcentaje configurable y caché", info.getDescription());
        assertEquals("1.0.0", info.getVersion());
    }
    
    @Test
    void customOpenAPI_ShouldCreateNewInstance_WhenCalledMultipleTimes() {
        // Act
        OpenAPI openAPI1 = openApiConfig.customOpenAPI();
        OpenAPI openAPI2 = openApiConfig.customOpenAPI();
        
        // Assert
        assertNotNull(openAPI1);
        assertNotNull(openAPI2);
        // Cada llamada debe crear una nueva instancia
        assertNotSame(openAPI1, openAPI2);
        
        // Pero el contenido debe ser igual
        assertEquals(openAPI1.getInfo().getTitle(), openAPI2.getInfo().getTitle());
        assertEquals(openAPI1.getInfo().getDescription(), openAPI2.getInfo().getDescription());
        assertEquals(openAPI1.getInfo().getVersion(), openAPI2.getInfo().getVersion());
    }
    
    @Test
    void customOpenAPI_ShouldHaveValidInfoStructure_WhenCreated() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        
        Info info = openAPI.getInfo();
        assertNotNull(info.getTitle());
        assertNotNull(info.getDescription());
        assertNotNull(info.getVersion());
        
        assertFalse(info.getTitle().isEmpty());
        assertFalse(info.getDescription().isEmpty());
        assertFalse(info.getVersion().isEmpty());
    }
}
