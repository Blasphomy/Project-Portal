package com.project.learning.service;

import com.project.common.exception.ResourceNotFoundException;
import com.project.learning.domain.Topic;
import com.project.learning.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    @Cacheable(value = "topic-cache", key = "'all'")
    public Flux<Topic> getAllTopics() {
        log.debug("Fetching all topics");
        return topicRepository.findAll();
    }

    public Mono<Topic> getTopicById(String id) {
        log.debug("Fetching topic by id: {}", id);
        return topicRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Topic", id)));
    }

    public Flux<Topic> getTopicsByDifficulty(String difficulty) {
        return topicRepository.findByDifficultyLevel(difficulty);
    }

    public Flux<Topic> getTopicsByCategory(String category) {
        return topicRepository.findByCategory(category);
    }

    public Mono<Topic> createTopic(Topic topic) {
        log.info("Creating topic: {}", topic.getName());
        return topicRepository.save(topic);
    }

    public Mono<Topic> updateTopic(String id, Topic topic) {
        return topicRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Topic", id)))
                .flatMap(existing -> {
                    topic.setId(id);
                    return topicRepository.save(topic);
                });
    }

    public Mono<Void> deleteTopic(String id) {
        return topicRepository.deleteById(id);
    }
}
