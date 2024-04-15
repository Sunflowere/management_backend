package com.jiannanzhi.managebd.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//Tell the Spring container that this is a configuration class
public class SwaggerConfig {
    //Visit the website:http://127.0.0.1:9090/swagger-ui/index.html
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot Vue Practice")
                        .description("SpringBoot+Vue Test Swagger debugging")
                        .version("v1"));
    }
}