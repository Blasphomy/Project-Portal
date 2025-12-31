package com.project.learning.repository;

import com.project.learning.domain.Quest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface QuestRepository extends R2dbcRepository<Quest, String> {
    Flux<Quest> findByTopicIdOrderBySequenceOrder(String topicId);
}
