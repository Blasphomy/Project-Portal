package com.project.user.controller;

import com.project.common.dto.ApiResponse;
import com.project.user.domain.User;
import com.project.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName())
                .map(user -> ApiResponse.success(
                        "Registration successful",
                        new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName())));
    }

    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        return userService.authenticateUser(request.getEmail(), request.getPassword())
                .flatMap(token -> userService.getUserByEmail(request.getEmail()).map(
                        user -> ApiResponse.success(
                                "Login successful",
                                new LoginResponse(
                                        token,
                                        new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                                                user.getFullName())))));
    }

    @GetMapping("/me")
    public Mono<ApiResponse<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // TODO: Extract user from JWT token
        return Mono.just(ApiResponse.success(new UserResponse("placeholder", "user", "email", "name")));
    }

    // DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
        private UserResponse user;
    }

    @Data
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String username;
        private String email;
        private String fullName;
    }
}
