package com.project.project_portal.handler;

import com.project.project_portal.dto.User;
import com.project.project_portal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * UserHandler manages HTTP request handling for user management operations.
 *
 * Provides endpoints for:
 * - User CRUD operations
 * - User retrieval by ID
 *
 * Delegates all business logic to UserService.
 */
@Tag(name = "Users", description = "User account management endpoints")
@Component
public class UserHandler {

    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    /**
     * Retrieves all users in the system.
     *
     * @param request ServerRequest
     * @return Mono<ServerResponse> with Flux<User>
     */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllUsers(), User.class);
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param request ServerRequest with id path variable
     * @return Mono<ServerResponse> with User or 404 if not found
     */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.getUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Creates a new user account.
     *
     * @param request ServerRequest with User in body
     * @return Mono<ServerResponse> with created User
     */
    @Operation(
            summary = "Create a new user",
            description = "Register a new user account in the system",
            tags = "Users"
    )
    @ApiResponse(responseCode = "200", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(service::createUser)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    /**
     * Updates an existing user's information.
     *
     * @param request ServerRequest with id path variable and User in body
     * @return Mono<ServerResponse> with updated User or 404 if not found
     */
    public Mono<ServerResponse> updateUser(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(User.class)
                .flatMap(user -> service.updateUser(id, user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Deletes a user account.
     *
     * @param request ServerRequest with id path variable
     * @return Mono<ServerResponse> with 204 No Content
     */
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.deleteUser(id)
                .then(ServerResponse.noContent().build());
    }
}