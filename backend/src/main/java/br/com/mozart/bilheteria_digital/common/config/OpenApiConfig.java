package br.com.mozart.bilheteria_digital.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bilheteria Digital API")
                        .version("1.0.0")
                        .description("API para eventos, reservas, pagamentos, ingressos e validacao de portaria."));
    }
}
