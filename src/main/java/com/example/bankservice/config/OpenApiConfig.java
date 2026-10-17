package com.example.bankservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Bank Service API")
                .description("Manage banks, branch addresses and customer accounts")
                .version("v1"));
    }
}
