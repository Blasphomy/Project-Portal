package com.project.project_portal.repo;

import com.project.project_portal.dto.Topic;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TopicRepository extends ReactiveCrudRepository<Topic, String> {

    Flux<Topic> findAll();
}