package com.project.user.service;

import com.project.user.service.dto.JwtAuthenticationResponse;
import com.project.user.service.dto.SignInRequest;
import com.project.user.service.dto.SignUpRequest;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<JwtAuthenticationResponse> signup(SignUpRequest request);
    Mono<JwtAuthenticationResponse> signin(SignInRequest request);
}
