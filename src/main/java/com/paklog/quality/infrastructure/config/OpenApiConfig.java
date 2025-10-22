package com.paklog.quality.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI qualityOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Quality Control & Compliance API")
                .description("Quality control and compliance for Paklog WMS/WES platform")
                .version("1.0.0"));
    }
}
