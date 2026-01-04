package com.project.user.controller;

import com.project.user.service.AuthenticationService;
import com.project.user.service.dto.JwtAuthenticationResponse;
import com.project.user.service.dto.SignInRequest;
import com.project.user.service.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ResponseEntity
                        .badRequest()
                        .body(Map.of("message", ex.getMessage()))))
                .onErrorResume(Exception.class, ex -> Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "An unexpected error occurred"))));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ResponseEntity
                        .badRequest()
                        .body(Map.of("message", ex.getMessage()))))
                .onErrorResume(Exception.class, ex -> Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "An unexpected error occurred"))));
    }
}
