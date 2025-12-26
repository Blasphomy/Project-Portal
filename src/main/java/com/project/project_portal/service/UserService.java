package com.project.project_portal.service;

import com.project.project_portal.dto.User;
import com.project.project_portal.repo.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * UserService handles user management operations including:
 * - Retrieving users (by ID or email)
 * - Creating and updating user accounts
 * - Deleting users
 *
 * This service manages the User entity and provides a clean
 * interface for user-related database operations.
 */
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all users from the system.
     *
     * @return Flux<User> of all users
     */
    public Flux<User> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user ID
     * @return Mono<User> or empty if not found
     */
    public Mono<User> getUserById(String id) {
        return repository.findById(id);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The user's email
     * @return Mono<User> or empty if not found
     */
    public Mono<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Creates a new user account.
     *
     * @param user The user to create
     * @return Mono<User> with persisted data
     */
    public Mono<User> createUser(User user) {
        return repository.save(user);
    }

    /**
     * Updates an existing user's information.
     * Merges updated fields with existing user data.
     *
     * @param id The user ID to update
     * @param user The updated user data
     * @return Mono<User> with merged data or empty if user not found
     */
    public Mono<User> updateUser(String id, User user) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(user.getName());
                    existing.setEmail(user.getEmail());
                    existing.setTotalXp(user.getTotalXp());
                    return repository.save(existing);
                });
    }

    /**
     * Deletes a user account.
     *
     * @param id The user ID to delete
     * @return Mono<Void>
     */
    public Mono<Void> deleteUser(String id) {
        return repository.deleteById(id);
    }
}