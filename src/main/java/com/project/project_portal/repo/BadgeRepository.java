package com.project.project_portal.repo;

import com.project.project_portal.dto.Badge;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BadgeRepository extends ReactiveCrudRepository<Badge, String> {
    Mono<Badge> findByName(String name);
    Flux<Badge> findAll();
}