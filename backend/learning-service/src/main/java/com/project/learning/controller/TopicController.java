package com.project.learning.controller;

import com.project.common.dto.ApiResponse;
import com.project.learning.domain.Topic;
import com.project.learning.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public Flux<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<Topic>> getTopicById(@PathVariable String id) {
        return topicService.getTopicById(id)
                .map(ApiResponse::success);
    }

    @GetMapping("/difficulty/{difficulty}")
    public Flux<Topic> getTopicsByDifficulty(@PathVariable String difficulty) {
        return topicService.getTopicsByDifficulty(difficulty);
    }

    @GetMapping("/category/{category}")
    public Flux<Topic> getTopicsByCategory(@PathVariable String category) {
        return topicService.getTopicsByCategory(category);
    }

    @PostMapping
    public Mono<ApiResponse<Topic>> createTopic(@RequestBody Topic topic) {
        return topicService.createTopic(topic)
                .map(created -> ApiResponse.success("Topic created successfully", created));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<Topic>> updateTopic(@PathVariable String id, @RequestBody Topic topic) {
        return topicService.updateTopic(id, topic)
                .map(updated -> ApiResponse.success("Topic updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteTopic(@PathVariable String id) {
        return topicService.deleteTopic(id)
                .then(Mono.just(ApiResponse.success("Topic deleted successfully", null)));
    }
}
