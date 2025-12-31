package com.project.user.controller;

import com.project.user.service.AuthenticationService;
import com.project.user.service.dto.JwtAuthenticationResponse;
import com.project.user.service.dto.SignInRequest;
import com.project.user.service.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<JwtAuthenticationResponse>> signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request).map(ResponseEntity::ok);
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<JwtAuthenticationResponse>> signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request).map(ResponseEntity::ok);
    }
}
