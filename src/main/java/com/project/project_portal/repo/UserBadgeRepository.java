package com.project.project_portal.repo;

import com.project.project_portal.dto.UserBadge;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserBadgeRepository extends ReactiveCrudRepository<UserBadge, String> {
    Flux<UserBadge> findByUserId(String userId);
    Mono<UserBadge> findByUserIdAndBadgeId(String userId, String badgeId);

    @Query("DELETE FROM user_badges WHERE user_id = :userId AND badge_id = :badgeId")
    Mono<Void> deleteByUserIdAndBadgeId(String userId, String badgeId);
}