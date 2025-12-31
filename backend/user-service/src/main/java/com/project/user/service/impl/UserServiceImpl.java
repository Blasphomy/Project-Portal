package com.project.user.service.impl;

import com.project.user.repository.UserRepository;
import com.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return username -> userRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(userDetails -> (UserDetails) userDetails);
    }
}
