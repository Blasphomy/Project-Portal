package com.project.user.repository;

import com.project.user.domain.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, String> {
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<User> findByEmail(String email);

    Mono<User> findByUsername(String username);

    Mono<User> findByOauthProviderAndOauthId(String provider, String oauthId);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByUsername(String username);
}
