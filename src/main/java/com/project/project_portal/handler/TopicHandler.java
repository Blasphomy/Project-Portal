package com.project.project_portal.handler;

import com.project.project_portal.dto.ErrorResponse;
import com.project.project_portal.dto.Topic;
import com.project.project_portal.dto.TopicTreeView;
import com.project.project_portal.service.TopicService;
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
 * TopicHandler manages HTTP request handling for topic and content organization.
 *
 * Provides endpoints for:
 * - Topic CRUD operations
 * - Topic tree view (hierarchical quest and task structure)
 * - Paginated topic listing
 *
 * Delegates all business logic to TopicService.
 */
@Tag(name = "Topics", description = "Learning topics and content structure endpoints")
@Component
public class TopicHandler {

    private final TopicService service;

    public TopicHandler(TopicService service) {
        this.service = service;
    }

    /**
     * Retrieves all topics with pagination support.
     * Query parameters: page (default 0), size (default 10)
     *
     * @param request ServerRequest with optional page and size query params
     * @return Mono<ServerResponse> with Flux<Topic> for requested page
     */
    @Operation(
            summary = "Get all topics",
            description = "Retrieve paginated list of learning topics",
            tags = "Topics"
    )
    @ApiResponse(responseCode = "200", description = "Topics retrieved successfully")
    public Mono<ServerResponse> getAllTopics(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllTopics(page, size), Topic.class);
    }

    /**
     * Retrieves a specific topic by ID.
     *
     * @param request ServerRequest with id path variable
     * @return Mono<ServerResponse> with Topic or 404 if not found
     */
    public Mono<ServerResponse> getTopicById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.getTopicById(id)
                .flatMap(topic -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(topic))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Retrieves the complete hierarchical tree for a topic.
     * Includes all quests and tasks organized in learning sequence.
     *
     * @param request ServerRequest with id path variable (topic ID)
     * @return Mono<ServerResponse> with TopicTreeView or 404 if not found
     */
    @Operation(
            summary = "Get topic hierarchy",
            description = "Retrieve complete topic structure with all quests and tasks",
            tags = "Topics"
    )
    @ApiResponse(responseCode = "200", description = "Topic tree retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicTreeView.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found")
    public Mono<ServerResponse> getTopicTree(ServerRequest request) {
        String topicId = request.pathVariable("id");
        return service.getTopicTree(topicId)
                .flatMap(tree -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tree))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Creates a new topic with validation.
     * Topic name is required and cannot be blank.
     *
     * @param request ServerRequest with Topic in body
     * @return Mono<ServerResponse> with created Topic or 400 if validation fails
     */
    public Mono<ServerResponse> createTopic(ServerRequest request) {
        return request.bodyToMono(Topic.class)
                .flatMap(topic -> {
                    // Validate required fields
                    if (topic.getName() == null || topic.getName().isBlank()) {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("Topic name is required"));
                    }
                    return service.createTopic(topic)
                            .flatMap(savedTopic -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(savedTopic));
                });
    }

    /**
     * Updates an existing topic.
     *
     * @param request ServerRequest with id path variable and Topic in body
     * @return Mono<ServerResponse> with updated Topic or 404 if not found
     */
    public Mono<ServerResponse> updateTopic(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Topic.class)
                .flatMap(topic -> service.updateTopic(id, topic))
                .flatMap(topic -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(topic))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Deletes a topic and all associated quests and tasks.
     *
     * @param request ServerRequest with id path variable
     * @return Mono<ServerResponse> with 204 No Content
     */
    public Mono<ServerResponse> deleteTopic(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.deleteTopic(id)
                .then(ServerResponse.noContent().build());
    }
}