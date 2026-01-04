package com.project.user.service.impl;

import com.project.user.domain.User;
import com.project.user.repository.UserRepository;
import com.project.user.service.AuthenticationService;
import com.project.user.service.JwtService;
import com.project.user.service.dto.JwtAuthenticationResponse;
import com.project.user.service.dto.SignInRequest;
import com.project.user.service.dto.SignUpRequest;
import com.project.user.service.dto.UserDTO;
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
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already exists."));
                    }
                    return userRepository.existsByUsername(request.getUsername());
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Username already exists."));
                    }
                    var user = User.builder()
                            .username(request.getUsername())
                            .fullName(request.getFullName())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .role(User.Role.USER)
                            .isActive(true)
                            .build();
                    return userRepository.save(user);
                })
                .map(savedUser -> {
                    var jwt = jwtService.generateToken(savedUser);
                    var userDTO = UserDTO.builder()
                            .id(savedUser.getId())
                            .username(savedUser.getUsername())
                            .fullName(savedUser.getFullName())
                            .email(savedUser.getEmail())
                            .avatarUrl(savedUser.getAvatarUrl())
                            .totalXp(savedUser.getTotalXp())
                            .level(savedUser.getLevel())
                            .build();
                    return JwtAuthenticationResponse.builder()
                            .accessToken(jwt)
                            .user(userDTO)
                            .build();
                });
    }

    @Override
    public Mono<JwtAuthenticationResponse> signin(SignInRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(userDetails -> passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .map(userDetails -> {
                    var jwt = jwtService.generateToken(userDetails);
                    var userDTO = UserDTO.builder()
                            .id(userDetails.getId())
                            .username(userDetails.getUsername())
                            .fullName(userDetails.getFullName())
                            .email(userDetails.getEmail())
                            .avatarUrl(userDetails.getAvatarUrl())
                            .totalXp(userDetails.getTotalXp())
                            .level(userDetails.getLevel())
                            .build();
                    return JwtAuthenticationResponse.builder()
                            .accessToken(jwt)
                            .user(userDTO)
                            .build();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password.")));
    }
}
