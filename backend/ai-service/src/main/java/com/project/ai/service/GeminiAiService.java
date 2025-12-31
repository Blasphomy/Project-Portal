package com.project.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ai.clients.UserProgressClient;
import com.project.ai.dto.SkillTreeResponse;
import com.project.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiAiService {

    private final WebClient geminiWebClient;
    private final UserProgressClient userProgressClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.service.api-key}")
    private String apiKey;

    @Value("${ai.service.model:gemini-1.5-flash}")
    private String model;

    @Value("${ai.service.max-tokens:8192}")
    private int maxTokens;

    @Value("${ai.service.temperature:0.4}")
    private double temperature;

    @Autowired
    public GeminiAiService(
            @Qualifier("geminiWebClient") WebClient geminiWebClient,
            UserProgressClient userProgressClient,
            ObjectMapper objectMapper) {
        this.geminiWebClient = geminiWebClient;
        this.userProgressClient = userProgressClient;
        this.objectMapper = objectMapper;
    }

    public Mono<SkillTreeResponse> generateSkillTree(String userGoal, String userId) {
        String prompt = buildGenesisPrompt(userGoal);
        return callGeminiApi(prompt)
                .flatMap(this::parseSkillTreeResponse)
                .doOnSuccess(skillTree -> {
                    // Add userId to the skill tree before sending
                    SkillTreeResponse skillTreeWithUser = new SkillTreeResponse(skillTree.title(), skillTree.description(), skillTree.quests(), skillTree.dependencies(), Long.valueOf(userId));
                    userProgressClient.createSkillTree(skillTreeWithUser);
                });
    }

    private Mono<SkillTreeResponse> parseSkillTreeResponse(String jsonResponse) {
        try {
            SkillTreeResponse response = objectMapper.readValue(jsonResponse, SkillTreeResponse.class);
            return Mono.just(response);
        } catch (JsonProcessingException e) {
            log.error("Error parsing SkillTreeResponse from Gemini: {}", e.getMessage());
            return Mono.error(new ServiceUnavailableException("Failed to parse AI response."));
        }
    }

    private String buildGenesisPrompt(String userGoal) {
        return String.format("'''...'''", userGoal); // Prompt omitted for brevity
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackResponse")
    @RateLimiter(name = "aiService")
    @Retry(name = "aiService")
    public Mono<String> callGeminiApi(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI API key not configured");
            return Mono.error(new ServiceUnavailableException("AI Service not configured."));
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxTokens,
                        "responseMimeType", "application/json"));

        return geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/" + model + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .map(this::extractTextFromResponse)
                .doOnError(error -> log.error("Gemini API error: {}", error.getMessage()));
    }

    private String extractTextFromResponse(String response) {
        // Find the start and end of the JSON block
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");
        if (jsonStart == -1 || jsonEnd == -1) {
            log.error("Could not find JSON object in Gemini response: {}", response);
            return "{}";
        }
        return response.substring(jsonStart, jsonEnd + 1);
    }

    private Mono<SkillTreeResponse> fallbackResponse(String userGoal, String userId, Exception ex) {
        log.warn("AI service fallback activated for goal '{}' with user ID {} due to: {}", userGoal, userId, ex.getMessage());
        return Mono.error(new ServiceUnavailableException(
                "The AI Game Master is currently resting. Please try again in a moment."));
    }
}
