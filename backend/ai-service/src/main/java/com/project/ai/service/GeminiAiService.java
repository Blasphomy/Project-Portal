package com.project.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
        log.info("🧪 Using hardcoded response for testing");

        // TEMPORARY: Hardcoded response to test learning-service without wasting API
        // quota
        // TODO: Uncomment the real API call below after fixing the response parsing
        /*
         * String prompt = buildGenesisPrompt(userGoal);
         * return callGeminiApi(prompt)
         * .flatMap(this::parseSkillTreeResponse)
         * .doOnNext(skillTree -> {
         * log.info("✅ AI Generated Skill Tree: {}", skillTree);
         * })
         * .doOnSuccess(skillTree -> {
         * SkillTreeResponse skillTreeWithUser = new
         * SkillTreeResponse(skillTree.title(),
         * skillTree.description(), skillTree.nodes(), skillTree.edges(),
         * userGoal, userId);
         * log.info("📤 Sending skill tree to learning-service for user: {}", userId);
         * userProgressClient.createSkillTree(skillTreeWithUser, userId);
         * });
         */

        // Hardcoded successful response from Gemini
        String hardcodedJson = """
                {
                  "title": "Hello World: Your First Programming Adventure",
                  "description": "Embark on a journey to create your very first program",
                  "userGoal": "build a hello world program",
                  "nodes": [
                    {"id": "quest-1", "title": "Choose Your Programming Language", "description": "Explore popular programming languages", "category": "fundamentals"},
                    {"id": "quest-2", "title": "Set Up Your Development Environment", "description": "Install necessary tools", "category": "tools"},
                    {"id": "quest-3", "title": "Write Hello World", "description": "Write your first program", "category": "fundamentals"}
                  ],
                  "edges": [
                    {"source": "quest-1", "target": "quest-2"},
                    {"source": "quest-2", "target": "quest-3"}
                  ]
                }
                """;

        return parseSkillTreeResponse(hardcodedJson)
                .doOnNext(skillTree -> {
                    log.info("✅ Hardcoded Skill Tree: {}", skillTree);
                })
                .doOnSuccess(skillTree -> {
                    SkillTreeResponse skillTreeWithUser = new SkillTreeResponse(skillTree.title(),
                            skillTree.description(), skillTree.nodes(), skillTree.edges(),
                            userGoal, userId);
                    log.info("📤 Sending skill tree to learning-service for user: {}", userId);
                    userProgressClient.createSkillTree(skillTreeWithUser, userId);
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
        return String.format(
                """
                        You are an expert programming educator and curriculum designer, known as the "AI Game Master."
                        Your task is to design a personalized learning path for a student based on their stated goal.

                        **Student's Goal:** "%s"

                        **Your Mission:**
                        Create a complete "Skill Tree" (a directed graph of learning quests) that will guide this student
                        from their current knowledge level to achieving their goal. This skill tree should be:

                        1. **Comprehensive but focused:** Include 6-15 quests (nodes) that cover essential skills
                        2. **Well-structured:** Define clear prerequisite relationships (edges) between quests
                        3. **Pedagogically sound:** Arrange quests in a logical learning progression from fundamentals to advanced
                        4. **Categorized:** Tag each quest with a category (e.g., "fundamentals", "backend", "frontend", "database", "deployment", "security")

                        **Quest Design Guidelines:**
                        - Each quest should represent a discrete, learnable skill or concept
                        - Quest titles should be clear and actionable (e.g., "Master REST API Design" not just "APIs")
                        - Descriptions should explain WHAT the student will learn and WHY it matters
                        - Only create dependencies where truly necessary (don't over-constrain the graph)
                        - Ensure there are some "root" quests with no prerequisites that the student can start immediately

                        **Output Format:**
                        You MUST respond with ONLY valid JSON in this exact structure (no markdown, no extra text):

                        {
                          "title": "A compelling title for this learning journey (max 100 chars)",
                          "description": "A motivating 1-2 sentence overview of what the student will achieve",
                          "quests": [
                            {
                              "id": "quest-1",
                              "title": "Clear, actionable quest name",
                              "description": "Detailed description of what this quest teaches (2-3 sentences)",
                              "category": "fundamentals|backend|frontend|database|deployment|security|testing|tools"
                            }
                          ],
                          "dependencies": [
                            {
                              "from": "quest-1",
                              "to": "quest-2"
                            }
                          ]
                        }

                        **Critical Rules:**
                        - Quest IDs must be unique and use format "quest-1", "quest-2", etc.
                        - The "from" field in dependencies represents the prerequisite quest
                        - The "to" field represents the quest that will be unlocked after completing "from"
                        - Ensure the graph is acyclic (no circular dependencies)
                        - Response must be ONLY the JSON object, nothing else

                        **Example:**
                        If the goal is "Build a blog with user authentication", you might create quests like:
                        - "quest-1": "HTML & CSS Basics" (category: fundamentals, no prerequisites)
                        - "quest-2": "JavaScript Fundamentals" (category: fundamentals, no prerequisites)
                        - "quest-3": "HTTP & REST APIs" (category: backend, depends on quest-2)
                        - "quest-4": "User Authentication with JWT" (category: security, depends on quest-3)
                        - etc.

                        Now, generate the skill tree for the student's goal above. Output ONLY valid JSON.
                        """,
                userGoal);
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackResponse")
    @RateLimiter(name = "aiService")
    @Retry(name = "aiService")
    public Mono<String> callGeminiApi(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("⚠️ AI API key not configured");
            return Mono.error(new ServiceUnavailableException("AI Service not configured."));
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxTokens));

        String requestUrl = "/models/" + model + ":generateContent?key=" + apiKey.substring(0, 10) + "...";
        log.info("🌐 Gemini API Request:");
        log.info("   URL: https://generativelanguage.googleapis.com/v1beta{}", requestUrl);
        log.info("   Model: {}", model);
        log.info("   Temperature: {}, MaxTokens: {}", temperature, maxTokens);
        log.info("   Prompt length: {} chars", prompt.length());
        log.info("   Request Body: {}", requestBody);

        return geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/" + model + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .doOnNext(response -> {
                    log.info("📥 Gemini API Raw Response (first 500 chars): {}",
                            response.length() > 500 ? response.substring(0, 500) + "..." : response);
                })
                .map(this::extractTextFromResponse)
                .doOnNext(extracted -> {
                    log.info("✂️ Extracted JSON from response: {}", extracted);
                })
                .doOnError(error -> log.info("❌ Gemini API error: {}", error.getMessage()));
    }

    private String extractTextFromResponse(String response) {
        try {
            // Parse the Gemini API response structure
            JsonNode root = objectMapper.readTree(response);

            // Navigate to candidates[0].content.parts[0].text
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                log.info("⚠️ No candidates in response");
                return "{}";
            }

            JsonNode text = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            if (text.isMissingNode()) {
                log.info("⚠️ No text found in response structure");
                return "{}";
            }

            String textContent = text.asText();
            log.info("📝 Raw text from AI: {}",
                    textContent.length() > 200 ? textContent.substring(0, 200) + "..." : textContent);

            // Remove markdown code blocks if present
            String cleaned = textContent.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7); // Remove ```json
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3); // Remove ```
            }

            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3); // Remove trailing ```
            }

            cleaned = cleaned.trim();
            log.info("🧹 Cleaned JSON (first 200 chars): {}",
                    cleaned.length() > 200 ? cleaned.substring(0, 200) + "..." : cleaned);

            return cleaned;

        } catch (Exception e) {
            log.info("⚠️ Error parsing Gemini response: {}", e.getMessage());
            // Fallback to old logic
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");
            if (jsonStart == -1 || jsonEnd == -1) {
                return "{}";
            }
            return response.substring(jsonStart, jsonEnd + 1);
        }
    }

    private Mono<SkillTreeResponse> fallbackResponse(String userGoal, String userId, Exception ex) {
        log.warn("AI service fallback activated for goal '{}' with user ID {} due to: {}", userGoal, userId,
                ex.getMessage());
        return Mono.error(new ServiceUnavailableException(
                "The AI Game Master is currently resting. Please try again in a moment."));
    }
}
