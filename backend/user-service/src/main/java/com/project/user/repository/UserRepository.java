package com.project.user.repository;

import com.project.user.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByEmail(String email);

    Mono<User> findByOauthProviderAndOauthId(String provider, String oauthId);

    Mono<Boolean> existsByEmail(String email);

}
