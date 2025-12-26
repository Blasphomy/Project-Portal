package com.project.project_portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * OpenAPIConfig configures Swagger/OpenAPI documentation for the Project Portal API.
 *
 * Provides:
 * - Swagger UI at http://localhost:8086/swagger-ui.html
 * - OpenAPI JSON at http://localhost:8086/v3/api-docs
 * - API documentation for all endpoints
 * - Interactive API testing interface
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configures the OpenAPI/Swagger documentation.
     * Sets up API title, description, version, contact, and server information.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI projectPortalAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Portal API")
                        .description("Interview Preparation Game - Learn Industry Tech with AI-Powered Content")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Adi Aggarwal")
                                .url("https://github.com/")
                                .email("adi.aggarwal@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))

                // Configure servers for different environments
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8086")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Alternative Local Server"),
                        new Server()
                                .url("https://api.yourdomain.com")
                                .description("Production Server (Update with actual domain)")
                ));
    }
}

