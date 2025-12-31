package com.project.learning.repository;

import com.project.learning.domain.Topic;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TopicRepository extends R2dbcRepository<Topic, String> {
    Flux<Topic> findByDifficultyLevel(String difficultyLevel);

    Flux<Topic> findByCategory(String category);
}
