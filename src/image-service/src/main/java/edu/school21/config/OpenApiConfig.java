package edu.school21.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    private static final String TITLE = "Image";
    private static final String VERSION = "1.0";
    private static final String DESCRIPTION = "Image Service";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/")).info(getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title(TITLE)
                .version(VERSION)
                .description(DESCRIPTION);
    }
}