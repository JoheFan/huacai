package com.huacai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI huacaiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("华彩系统 API")
                        .description("一期 MVP 接口骨架")
                        .version("v1"));
    }
}
