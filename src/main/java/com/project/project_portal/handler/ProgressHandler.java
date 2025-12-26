package com.project.project_portal.handler;

import com.project.project_portal.dto.ErrorResponse;
import com.project.project_portal.dto.UserTaskProgress;
import com.project.project_portal.dto.UserQuestProgress;
import com.project.project_portal.service.ProgressDomainService;
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
 * ProgressHandler manages all HTTP request handling for game progress operations.
 *
 * Coordinates between:
 * - Task starting and completion
 * - Quest progress tracking
 * - User XP and badge awards
 * - Completion status queries
 *
 * All game state updates are delegated to ProgressDomainService for consistency.
 */
@Tag(name = "Progress", description = "Game progress and task/quest management endpoints")
@Component
public class ProgressHandler {

    private final ProgressDomainService progressDomainService;

    public ProgressHandler(ProgressDomainService progressDomainService) {
        this.progressDomainService = progressDomainService;
    }

    /**
     * Starts a task for a user.
     * Creates initial task progress record with IN_PROGRESS status.
     * Creates quest progress record if this is the first task in a quest.
     *
     * @param request ServerRequest with taskId path variable and userId query param
     * @return Mono<ServerResponse> with UserTaskProgress or error
     */
    @Operation(
            summary = "Start a task",
            description = "Initialize a task for a user. Creates IN_PROGRESS status record.",
            tags = "Progress"
    )
    @ApiResponse(responseCode = "200", description = "Task started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTaskProgress.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request or task not found")
    public Mono<ServerResponse> startTask(
            @Parameter(description = "Task ID to start", required = true) ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        String userId = request.queryParam("userId")
                .orElseThrow(() -> new IllegalArgumentException("userId is required"));

        return progressDomainService.startTask(userId, taskId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.getMessage())));
    }

    /**
     * Completes a task for a user.
     * Awards XP, updates quest progress, and checks for badge milestones.
     *
     * @param request ServerRequest with taskId path variable and userId query param
     * @return Mono<ServerResponse> with completed UserTaskProgress or error
     */
    @Operation(
            summary = "Complete a task",
            description = "Mark task as completed. Awards XP and checks for badge milestones.",
            tags = "Progress"
    )
    @ApiResponse(responseCode = "200", description = "Task completed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTaskProgress.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request or task not started")
    public Mono<ServerResponse> completeTask(
            @Parameter(description = "Task ID to complete", required = true) ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        String userId = request.queryParam("userId")
                .orElseThrow(() -> new IllegalArgumentException("userId is required"));

        return progressDomainService.completeTask(userId, taskId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.getMessage())));
    }

    /**
     * Retrieves task progress for a specific user-task combination.
     *
     * @param request ServerRequest with userId and taskId path variables
     * @return Mono<ServerResponse> with UserTaskProgress or 404 if not found
     */
    public Mono<ServerResponse> getUserTaskProgress(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String taskId = request.pathVariable("taskId");

        return progressDomainService.getUserTaskProgress(userId, taskId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Retrieves quest progress for a specific user-quest combination.
     *
     * @param request ServerRequest with userId and questId path variables
     * @return Mono<ServerResponse> with UserQuestProgress or 404 if not found
     */
    public Mono<ServerResponse> getUserQuestProgress(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String questId = request.pathVariable("questId");

        return progressDomainService.getUserQuestProgress(userId, questId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Retrieves all quest progress records for a user.
     *
     * @param request ServerRequest with userId path variable
     * @return Mono<ServerResponse> with Flux of UserQuestProgress
     */
    public Mono<ServerResponse> getAllUserQuestProgress(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(progressDomainService.getAllUserQuestProgress(userId), UserQuestProgress.class);
    }

    /**
     * Retrieves all task progress records for a user.
     *
     * @param request ServerRequest with userId path variable
     * @return Mono<ServerResponse> with Flux of UserTaskProgress
     */
    public Mono<ServerResponse> getAllUserTaskProgress(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(progressDomainService.getAllUserTaskProgress(userId), UserTaskProgress.class);
    }

    /**
     * Retrieves a detailed view of a quest including all its tasks and user progress.
     * Useful for quest overview/dashboard displays.
     *
     * @param request ServerRequest with userId and questId path variables
     * @return Mono<ServerResponse> with hierarchical quest+tasks+progress or error
     */
    public Mono<ServerResponse> getUserQuestWithTasks(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String questId = request.pathVariable("questId");

        return progressDomainService.getUserQuestWithTaskProgress(userId, questId)
                .flatMap(questView -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(questView))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.toString())));
    }

    /**
     * Retrieves comprehensive user completion statistics.
     * Shows tasks completed, quests completed, XP earned, badges earned, completion flags.
     *
     * @param request ServerRequest with userId path variable
     * @return Mono<ServerResponse> with completion status Map or error
     */
    @Operation(
            summary = "Get user completion status",
            description = "Retrieve comprehensive completion statistics including XP, badges, and progress",
            tags = "Progress"
    )
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    @ApiResponse(responseCode = "400", description = "User not found")
    public Mono<ServerResponse> getUserCompletionStatus(
            @Parameter(description = "User ID", required = true) ServerRequest request) {
        String userId = request.pathVariable("userId");

        return progressDomainService.getUserCompletionStatus(userId)
                .flatMap(status -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(status))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.toString())));
    }

    /**
     * Awards mastery badges when user completes all content.
     * Checks for "Legend Master" (all tasks+quests) and "Java Master" (all quests).
     *
     * @param request ServerRequest with userId path variable
     * @return Mono<ServerResponse> with updated completion status or error
     */
    public Mono<ServerResponse> awardMasteryBadges(ServerRequest request) {
        String userId = request.pathVariable("userId");

        return progressDomainService.awardMasteryBadges(userId)
                .then(progressDomainService.getUserCompletionStatus(userId))
                .flatMap(status -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(status))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.toString())));
    }
}