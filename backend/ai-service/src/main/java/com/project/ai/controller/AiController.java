
package com.project.ai.controller;

import com.project.ai.dto.GenerateSkillTreeRequest;
import com.project.ai.dto.SkillTreeResponse;
//import com.project.ai.service.SemanticSearchService;
import com.project.ai.service.GeminiAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * AI Service REST Controller.
 * Handles AI-powered generation for the application.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiAiService aiService;
//    private final SemanticSearchService semanticSearchService;

    /**
     * Generates a personalized skill tree for a user based on their stated goal.
     * This is the "Genesis Engine" of the application, responsible for creating the
     * user's unique learning adventure map.
     *
     * @param request The request containing the user's high-level goal.
     * @param userId  The ID of the user, passed in a request header.
     * @return A Mono containing the structured SkillTreeResponse, representing the quest graph.
     */
    @PostMapping("/generate-skill-tree")
    public Mono<SkillTreeResponse> generateSkillTree(
            @RequestBody GenerateSkillTreeRequest request,
            @RequestHeader("X-User-Id") String userId) {
        // Pass the user ID to the service layer
        return aiService.generateSkillTree(request.goal(), userId);
    }

//    @GetMapping("/search")
//    public Mono<List<String>> semanticSearch(@RequestParam String query) {
//        return Mono.just(semanticSearchService.semanticSearch(query, 5));
//    }

    /**
     * LEGACY - This was used for generating simple text content and will be phased out.
     */
    @PostMapping("/generate")
    public Mono<String> generateContent(@RequestBody PromptRequest request) {
        return aiService.callGeminiApi(request.getPrompt());
    }

    /**
     * Health check endpoint to ensure the service is running.
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("AI Service is running with Gemini API");
    }

    // DTO for the legacy generate endpoint
    public static class PromptRequest {
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
}
