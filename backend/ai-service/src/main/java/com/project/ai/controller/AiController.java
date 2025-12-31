package com.project.ai.controller;

import com.project.ai.service.GeminiAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * AI Service REST Controller for testing
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiAiService aiService;

    /**
     * Test endpoint to generate content
     */
    @PostMapping("/generate")
    public Mono<String> generateContent(@RequestBody PromptRequest request) {
        return aiService.callGeminiApi(request.getPrompt());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("AI Service is running with Gemini API");
    }

    // DTO
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
