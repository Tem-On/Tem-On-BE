package com.example.temon.commerceservice.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName,
                        new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        Server gatewayServer = new Server()
                .url("http://localhost:8080")
                .description("Gateway");

        return new OpenAPI()
                .servers(List.of(gatewayServer))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}