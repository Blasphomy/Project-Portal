package com.project.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Enhanced AI Service with Resilience4j patterns
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.service.api-key}")
    private String apiKey;

    @Value("${ai.service.model:gemini-1.5-flash}")
    private String model;

    @Value("${ai.service.max-tokens:2000}")
    private int maxTokens;

    @Value("${ai.service.temperature:0.7}")
    private double temperature;

    /**
     * Call Gemini API with circuit breaker, rate limiting, and retry
     */
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackResponse")
    @RateLimiter(name = "aiService")
    @Retry(name = "aiService")
    public Mono<String> callGeminiApi(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI API key not configured");
            return Mono.error(new ServiceUnavailableException("AI Service"));
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxTokens,
                        "responseMimeType", "application/json"));

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/" + model + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .map(this::extractTextFromResponse)
                .doOnError(error -> log.error("Gemini API error: {}", error.getMessage()));
    }

    /**
     * Fallback when circuit is open or API fails
     */
    private Mono<String> fallbackResponse(String prompt, Exception ex) {
        log.warn("Using fallback response due to: {}", ex.getMessage());
        return Mono.just(createFallbackJson());
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.at("/candidates/0/content/parts/0/text").asText();
        } catch (Exception e) {
            log.error("Error extracting text from Gemini response", e);
            return createFallbackJson();
        }
    }

    private String createFallbackJson() {
        return """
                {
                  "introduction": "AI service temporarily unavailable. Please try again later.",
                  "concepts": [],
                  "examples": [],
                  "commonMistakes": [],
                  "exercises": [],
                  "resources": []
                }
                """;
    }

    /**
     * Build study material generation prompt
     */
    public String buildStudyMaterialPrompt(String topicName, String questName,
            String questDescription, List<String> taskList) {
        return String.format("""
                Generate comprehensive study material for a complete programming beginner learning %s.

                Quest: %s
                Description: %s
                Tasks: %s

                Provide the response in this exact JSON format:
                {
                  "introduction": "A welcoming introduction to the quest (2-3 sentences)",
                  "concepts": [
                    {
                      "title": "Concept name",
                      "explanation": "Clear explanation",
                      "keyPoints": ["point 1", "point 2", "point 3"]
                    }
                  ],
                  "examples": [
                    {
                      "title": "Example title",
                      "code": "Code snippet",
                      "explanation": "What the code does",
                      "language": "java"
                    }
                  ],
                  "commonMistakes": ["mistake 1", "mistake 2", "mistake 3"],
                  "exercises": ["exercise 1", "exercise 2"],
                  "resources": [
                    {
                      "title": "Resource title",
                      "url": "https://...",
                      "description": "Brief description",
                      "linkType": "article"
                    }
                  ]
                }

                Make explanations beginner-friendly with real-world analogies.
                """, topicName, questName, questDescription, String.join(", ", taskList));
    }

    /**
     * Build code hints prompt
     */
    public String buildHintPrompt(String taskTitle, String taskDescription,
            String userCode, String language) {
        return String.format("""
                A programming beginner is working on: %s
                Description: %s
                Language: %s

                Their current code:
                ```
                %s
                ```

                Provide 3 progressive hints in this JSON format:
                {
                  "hints": [
                    { "level": 1, "hintText": "Conceptual hint (what approach to use)", "type": "concept" },
                    { "level": 2, "hintText": "Structural hint (what code structure needed)", "type": "syntax" },
                    { "level": 3, "hintText": "Specific hint (more direct guidance)", "type": "logic" }
                  ],
                  "taskContext": "Brief reminder of what the task requires"
                }

                Don't give the complete solution. Help them learn by thinking.
                """, taskTitle, taskDescription, language, userCode);
    }

    /**
     * Build code review prompt
     */
    public String buildCodeReviewPrompt(String taskTitle, String taskDescription,
            String userCode, String language) {
        return String.format("""
                Review this %s code for a beginner programmer.

                Task: %s
                Description: %s

                Code submitted:
                ```
                %s
                ```

                Provide review in this JSON format:
                {
                  "overallAssessment": "excellent/good/needswork/incorrect",
                  "score": 85,
                  "feedback": [
                    { "category": "correctness", "severity": "minor", "message": "Feedback message", "lineNumber": 5 }
                  ],
                  "strengths": ["strength 1", "strength 2"],
                  "improvements": ["improvement 1", "improvement 2"],
                  "passesTests": true
                }

                Be encouraging but honest. Focus on learning.
                """, language, taskTitle, taskDescription, userCode);
    }
}
