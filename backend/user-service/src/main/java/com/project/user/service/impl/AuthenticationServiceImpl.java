package com.project.user.service.impl;

import com.project.user.domain.User;
import com.project.user.repository.UserRepository;
import com.project.user.service.AuthenticationService;
import com.project.user.service.JwtService;
import com.project.user.service.dto.JwtAuthenticationResponse;
import com.project.user.service.dto.SignInRequest;
import com.project.user.service.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<JwtAuthenticationResponse> signup(SignUpRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();
        return userRepository.save(user)
                .map(savedUser -> {
                    var jwt = jwtService.generateToken(savedUser);
                    return JwtAuthenticationResponse.builder().token(jwt).build();
                });
    }

    @Override
    public Mono<JwtAuthenticationResponse> signin(SignInRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .map(userDetails -> {
                    var jwt = jwtService.generateToken(userDetails);
                    return JwtAuthenticationResponse.builder().token(jwt).build();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password.")));
    }
}
