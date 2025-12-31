package com.project.learning.repository;

import com.project.learning.domain.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaskRepository extends R2dbcRepository<Task, String> {
    Flux<Task> findByQuestIdOrderBySequenceOrder(String questId);
}
