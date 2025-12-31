package com.project.learning.repository;

import com.project.learning.domain.UserProgress;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserProgressRepository extends R2dbcRepository<UserProgress, String> {
    Flux<UserProgress> findByUserId(String userId);

    Mono<UserProgress> findByUserIdAndTaskId(String userId, String taskId);

    Flux<UserProgress> findByUserIdAndStatus(String userId, String status);
}
