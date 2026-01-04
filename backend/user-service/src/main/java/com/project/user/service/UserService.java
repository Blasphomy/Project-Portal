package com.project.user.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserService {
    ReactiveUserDetailsService reactiveUserDetailsService();

    Mono<UserDetails> findByUsername(String username);
}
