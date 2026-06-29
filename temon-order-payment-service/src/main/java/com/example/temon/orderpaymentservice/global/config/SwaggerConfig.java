package com.example.temon.orderpaymentservice.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement =
                new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME);

        Server gatewayServer = new Server()
                .url("http://localhost:8080")
                .description("Gateway");

        return new OpenAPI()
                .info(new Info()
                        .title("Tem-On Order-Payment Service API") 
                        .version("v1"))
                .servers(List.of(gatewayServer))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        securityScheme
                                )
                )
                .addSecurityItem(securityRequirement);
    }
}