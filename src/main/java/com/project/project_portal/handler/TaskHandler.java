package com.project.project_portal.handler;

import com.project.project_portal.dto.Task;
import com.project.project_portal.service.TaskService;
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
 * TaskHandler manages HTTP request handling for task operations.
 *
 * Provides endpoints for:
 * - Retrieving tasks
 * - Retrieving tasks by quest
 * - CRUD operations on tasks
 */
@Tag(name = "Tasks", description = "Task management endpoints")
@Component
public class TaskHandler {

    private final TaskService service;

    public TaskHandler(TaskService service) {
        this.service = service;
    }

    /**
     * Retrieves all tasks in the system.
     *
     * @param request ServerRequest
     * @return Mono<ServerResponse> with Flux<Task>
     */
    @Operation(
            summary = "Get all tasks",
            description = "Retrieve list of all available tasks",
            tags = "Tasks"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public Mono<ServerResponse> getAllTasks(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllTasks(), Task.class);
    }

    /**
     * Retrieves a specific task by ID.
     *
     * @param request ServerRequest with task id path variable
     * @return Mono<ServerResponse> with Task or 404 if not found
     */
    @Operation(
            summary = "Get task by ID",
            description = "Retrieve a specific task details",
            tags = "Tasks"
    )
    @ApiResponse(responseCode = "200", description = "Task retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Mono<ServerResponse> getTaskById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.getTaskById(id)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Retrieves all tasks for a specific quest.
     *
     * @param request ServerRequest with questId path variable
     * @return Mono<ServerResponse> with Flux<Task> ordered by sequence
     */
    @Operation(
            summary = "Get tasks by quest",
            description = "Retrieve all tasks for a specific quest",
            tags = "Tasks"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public Mono<ServerResponse> getTasksByQuestId(ServerRequest request) {
        String questId = request.pathVariable("questId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getTasksByQuestId(questId), Task.class);
    }
    public Mono<ServerResponse> createTask(ServerRequest request) {
        return request.bodyToMono(Task.class)
                .flatMap(service::createTask)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(task));
    }

    public Mono<ServerResponse> updateTask(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Task.class)
                .flatMap(task -> service.updateTask(id, task))
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteTask(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.deleteTask(id)
                .then(ServerResponse.noContent().build());
    }
}