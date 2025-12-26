package com.project.project_portal.repo;

import com.project.project_portal.dto.Quest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface QuestRepository extends ReactiveCrudRepository<Quest, String> {

    Flux<Quest> findByTopicIdOrderByOrderIndexAsc(String topicId);
}