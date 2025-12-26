package com.project.project_portal.handler;

import com.project.project_portal.dto.ErrorResponse;
import com.project.project_portal.dto.UserTaskProgress;
import com.project.project_portal.service.UserTaskProgressService;
import com.project.project_portal.service.ProgressDomainService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserTaskProgressHandler {

    private final UserTaskProgressService service;
    private final ProgressDomainService progressDomainService;

    public UserTaskProgressHandler(UserTaskProgressService service, ProgressDomainService progressDomainService) {
        this.service = service;
        this.progressDomainService = progressDomainService;
    }

    public Mono<ServerResponse> completeTask(ServerRequest request) {
        String taskId = request.pathVariable("taskId");
        String userId = request.queryParam("userId").orElse(null);

        if (userId == null || userId.isBlank()) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse("userId query parameter is required"));
        }

        return progressDomainService.completeTask(userId, taskId)
                .flatMap(progress -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(progress))
                .onErrorResume(e -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse(e.getMessage())));
    }

    public Mono<ServerResponse> getProgressForUser(ServerRequest request) {
        String userId = request.pathVariable("userId");
        Flux<UserTaskProgress> progress = service.getByUser(userId);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(progress, UserTaskProgress.class);
    }

    public Mono<ServerResponse> getProgressForUserAndTask(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String taskId = request.pathVariable("taskId");
        return service.getByUserAndTask(userId, taskId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> upsertProgress(ServerRequest request) {
        Mono<UserTaskProgress> body = request.bodyToMono(UserTaskProgress.class);
        return body
                .flatMap(service::save)
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }
}