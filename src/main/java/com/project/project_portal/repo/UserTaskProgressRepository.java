package com.project.project_portal.repo;

import com.project.project_portal.dto.UserTaskProgress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserTaskProgressRepository extends ReactiveCrudRepository<UserTaskProgress, String> {
    Mono<UserTaskProgress> findByUserIdAndTaskId(String userId, String taskId);
    Flux<UserTaskProgress> findByUserId(String userId);
}