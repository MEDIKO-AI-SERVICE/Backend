package com.mediko.mediko_server.global.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "Bearer Authentication";

        SecurityScheme bearerAuth = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(jwtSchemeName, bearerAuth))
                .security(List.of(securityRequirement))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Mediko Springdoc")
                .description("Springdoc을 사용한 Mediko Swagger UI")
                .version("1.0.0");
    }
}