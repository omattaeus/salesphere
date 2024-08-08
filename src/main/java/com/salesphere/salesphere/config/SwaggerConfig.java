package com.salesphere.salesphere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Salesphere API")
                        .version("v1")
                        .description("API for managing products and stock in Salesphere!")
                        .termsOfService("https://salesphere.example.com/terms")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://salesphere.example.com/license")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Salesphere API")
                .pathsToMatch("/products/**",
                        "/websocket/**",
                        "/stock-updates")
                .build();
    }
}