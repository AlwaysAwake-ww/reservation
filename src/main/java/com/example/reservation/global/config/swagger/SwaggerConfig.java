package com.example.reservation.global.config.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){


        return new OpenAPI()
                .info(new Info()
                        .title("Template API")
                        .version("1.0.0")
                        .description("API Description"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Host")

                ));
    }
}
