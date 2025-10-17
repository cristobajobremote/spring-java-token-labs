package com.tokenlabs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Java Token Labs API")
                        .version("1.0.0")
                        .description("API REST para cálculos con porcentajes externos y caché Redis")
                        .contact(new Contact()
                                .name("Token Labs Team")
                                .email("contact@tokenlabs.com")
                                .url("https://tokenlabs.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
