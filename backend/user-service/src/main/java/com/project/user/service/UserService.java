package com.project.user.service;

import com.project.common.exception.ResourceNotFoundException;
import com.project.common.exception.ValidationException;
import com.project.common.util.IdGenerator;
import com.project.user.domain.User;
import com.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * User authentication and management service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    /**
     * Register new user with email/password
     */
    public Mono<User> registerUser(String username, String email, String password, String fullName) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ValidationException("email", "Email already registered"));
                    }
                    return userRepository.existsByUsername(username);
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ValidationException("username", "Username already taken"));
                    }

                    User user = User.builder()
                            .id(IdGenerator.generatePrefixedId("user"))
                            .username(username)
                            .email(email)
                            .passwordHash(passwordEncoder.encode(password))
                            .fullName(fullName)
                            .totalXp(0)
                            .level(1)
                            .isActive(true)
                            .isEmailVerified(false)
                            .build();

                    return userRepository.save(user);
                })
                .doOnSuccess(user -> log.info("User registered: {}", user.getEmail()));
    }

    /**
     * Authenticate user and generate tokens
     */
    public Mono<String> authenticateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ValidationException("Invalid email or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        return Mono.error(new ValidationException("Invalid email or password"));
                    }

                    if (!user.getIsActive()) {
                        return Mono.error(new ValidationException("Account is disabled"));
                    }

                    // Update last login
                    user.setLastLoginAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .map(savedUser -> jwtTokenService.generateToken(
                                    savedUser.getId(),
                                    savedUser.getEmail(),
                                    "USER"));
                })
                .doOnSuccess(token -> log.info("User authenticated: {}", email));
    }

    /**
     * Find or create user from OAuth provider
     */
    public Mono<User> findOrCreateOAuthUser(String provider, String oauthId,
            String email, String fullName, String avatarUrl) {
        return userRepository.findByOauthProviderAndOauthId(provider, oauthId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Creating new OAuth user: {} from {}", email, provider);

                    User user = User.builder()
                            .id(IdGenerator.generatePrefixedId("user"))
                            .username(email.split("@")[0] + "-" + IdGenerator.generateShortId(4))
                            .email(email)
                            .fullName(fullName)
                            .avatarUrl(avatarUrl)
                            .oauthProvider(provider)
                            .oauthId(oauthId)
                            .totalXp(0)
                            .level(1)
                            .isActive(true)
                            .isEmailVerified(true)
                            .build();

                    return userRepository.save(user);
                }))
                .doOnNext(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userRepository.save(user).subscribe();
                });
    }

    /**
     * Get user by ID
     */
    public Mono<User> getUserById(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)));
    }

    /**
     * Get user by email
     */
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", email)));
    }

    /**
     * Update user XP and level
     */
    public Mono<User> addXp(String userId, Integer xpAmount) {
        return getUserById(userId)
                .flatMap(user -> {
                    int newXp = user.getTotalXp() + xpAmount;
                    int newLevel = calculateLevel(newXp);

                    user.setTotalXp(newXp);
                    user.setLevel(newLevel);
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                });
    }

    private int calculateLevel(int totalXp) {
        // Simple level calculation: level = sqrt(xp / 100)
        return (int) Math.floor(Math.sqrt(totalXp / 100.0)) + 1;
    }
}
