package com.project.project_portal.repo;

import com.project.project_portal.dto.Task;
import com.project.project_portal.dto.TopicTreeView;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveCrudRepository<Task, String> {
    Flux<Task> findByQuestId(String questId);
    Flux<Task> findByQuestIdOrderByOrderIndexAsc(String questId);
}