package com.ford.intrastat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI intrastatOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Intrastat Demonstrator API")
                        .description("REST API for Ford of Europe Intrastat reporting demonstrator")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ford of Europe")
                                .email("intrastat-demo@ford.com")));
    }
}
