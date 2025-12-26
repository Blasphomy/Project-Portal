package com.project.project_portal.handler;

import com.project.project_portal.dto.Badge;
import com.project.project_portal.service.BadgeService;
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
 * BadgeHandler manages HTTP request handling for achievement and badge operations.
 *
 * Provides endpoints for:
 * - Retrieving all available badges
 * - Fetching user's earned badges
 * - Creating new badge types (admin operation)
 */
@Tag(name = "Badges", description = "Achievement badges and rewards endpoints")
@Component
public class BadgeHandler {

    private final BadgeService service;

    public BadgeHandler(BadgeService service) {
        this.service = service;
    }

    /**
     * Retrieves all available badges in the system.
     *
     * @param request ServerRequest
     * @return Mono<ServerResponse> with Flux<Badge>
     */
    @Operation(
            summary = "Get all available badges",
            description = "Retrieve list of all badges that can be earned",
            tags = "Badges"
    )
    @ApiResponse(responseCode = "200", description = "Badges retrieved successfully")
    public Mono<ServerResponse> getAllBadges(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllBadges(), Badge.class);
    }

    /**
     * Retrieves a specific badge by ID.
     *
     * @param request ServerRequest with badge id path variable
     * @return Mono<ServerResponse> with Badge or 404 if not found
     */
    @Operation(
            summary = "Get badge by ID",
            description = "Retrieve a specific badge details",
            tags = "Badges"
    )
    @ApiResponse(responseCode = "200", description = "Badge retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Badge not found")
    public Mono<ServerResponse> getBadgeById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.getBadgeById(id)
                .flatMap(badge -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(badge))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Creates a new badge type in the system.
     * Admin operation for adding new achievement badges.
     *
     * @param request ServerRequest with Badge in body
     * @return Mono<ServerResponse> with created Badge
     */
    @Operation(
            summary = "Create a new badge",
            description = "Admin operation to create a new achievement badge type",
            tags = "Badges"
    )
    @ApiResponse(responseCode = "200", description = "Badge created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid badge data")
    public Mono<ServerResponse> createBadge(ServerRequest request) {
        return request.bodyToMono(Badge.class)
                .flatMap(service::createBadge)
                .flatMap(badge -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(badge));
    }

    public Mono<ServerResponse> getUserBadges(ServerRequest request) {
        String userId = request.pathVariable("userId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getUserBadges(userId), Badge.class);
    }

    public Mono<ServerResponse> awardBadge(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String badgeId = request.pathVariable("badgeId");

        return service.awardBadgeToUser(userId, badgeId)
                .flatMap(userBadge -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userBadge))
                .onErrorResume(e -> ServerResponse.badRequest().build());
    }
}