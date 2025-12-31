
package com.project.ai.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${learning.service.url}")
    private String learningServiceUrl;

    @Bean
    @Qualifier("geminiWebClient")
    public WebClient geminiWebClient(@Value("https://generativelanguage.googleapis.com/v1beta") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @Qualifier("learningServiceWebClient")
    public WebClient learningServiceWebClient() {
        return WebClient.builder()
                .baseUrl(learningServiceUrl)
                .build();
    }
}
