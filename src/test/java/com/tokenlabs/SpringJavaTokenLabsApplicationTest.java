package com.tokenlabs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SpringJavaTokenLabsApplicationTest {
    
    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se carga correctamente
        // Si hay algún problema de configuración, esta prueba fallará
    }
}
