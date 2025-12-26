package com.project.project_portal.handler;

import com.project.project_portal.dto.Quest;
import com.project.project_portal.service.QuestService;
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
 * QuestHandler manages HTTP request handling for quest operations.
 *
 * Provides endpoints for:
 * - Retrieving quests
 * - Retrieving quests by topic
 * - CRUD operations on quests
 */
@Tag(name = "Quests", description = "Quest management endpoints")
@Component
public class QuestHandler {

    private final QuestService service;

    public QuestHandler(QuestService service) {
        this.service = service;
    }

    /**
     * Retrieves all quests in the system.
     *
     * @param request ServerRequest
     * @return Mono<ServerResponse> with Flux<Quest>
     */
    @Operation(
            summary = "Get all quests",
            description = "Retrieve list of all available quests",
            tags = "Quests"
    )
    @ApiResponse(responseCode = "200", description = "Quests retrieved successfully")
    public Mono<ServerResponse> getAllQuests(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllQuests(), Quest.class);
    }

    /**
     * Retrieves a specific quest by ID.
     *
     * @param request ServerRequest with quest id path variable
     * @return Mono<ServerResponse> with Quest or 404 if not found
     */
    @Operation(
            summary = "Get quest by ID",
            description = "Retrieve a specific quest details",
            tags = "Quests"
    )
    @ApiResponse(responseCode = "200", description = "Quest retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Quest not found")
    public Mono<ServerResponse> getQuestById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.getQuestById(id)
                .flatMap(quest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quest))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Retrieves all quests for a specific topic.
     *
     * @param request ServerRequest with topicId path variable
     * @return Mono<ServerResponse> with Flux<Quest> ordered by sequence
     */
    @Operation(
            summary = "Get quests by topic",
            description = "Retrieve all quests for a specific learning topic",
            tags = "Quests"
    )
    @ApiResponse(responseCode = "200", description = "Quests retrieved successfully")
    public Mono<ServerResponse> getQuestsByTopicId(ServerRequest request) {
        String topicId = request.pathVariable("topicId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getQuestsByTopicId(topicId), Quest.class);
    }
    public Mono<ServerResponse> createQuest(ServerRequest request) {
        return request.bodyToMono(Quest.class)
                .flatMap(service::createQuest)
                .flatMap(quest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quest));
    }

    public Mono<ServerResponse> updateQuest(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Quest.class)
                .flatMap(quest -> service.updateQuest(id, quest))
                .flatMap(quest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quest))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteQuest(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.deleteQuest(id)
                .then(ServerResponse.noContent().build());
    }
}