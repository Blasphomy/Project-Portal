package com.project.project_portal.repo;

import com.project.project_portal.dto.UserQuestProgress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserQuestProgressRepository extends ReactiveCrudRepository<UserQuestProgress, String> {
    Mono<UserQuestProgress> findByUserIdAndQuestId(String userId, String questId);
    Flux<UserQuestProgress> findByUserId(String userId);
}