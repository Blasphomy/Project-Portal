package com.project.project_portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

/**
 * CorsConfig configures Cross-Origin Resource Sharing (CORS) for the application.
 *
 * Allows requests from:
 * - http://localhost:3000 (React frontend)
 * - http://localhost:8080 (Alternative backend)
 * - http://localhost:8086 (Local development app)
 * - http://localhost:63342 (IntelliJ built-in server)
 *
 * For production, update allowedOrigins with actual frontend domain.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS web filter bean with configured origins and methods.
     *
     * @return CorsWebFilter configured for development
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Update these origins for production deployment
        corsConfig.setAllowedOrigins(java.util.Arrays.asList(
            "http://localhost:3000",      // React development server
            "http://localhost:8080",      // Alternative backend
            "http://localhost:8086",      // Local app instance
            "http://localhost:63342"      // IntelliJ built-in server
        ));
        corsConfig.setAllowedMethods(java.util.Arrays.asList("*"));
        corsConfig.setAllowedHeaders(java.util.Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);  // Cache CORS preflight response for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}