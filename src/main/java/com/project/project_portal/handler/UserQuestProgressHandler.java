package com.project.project_portal.handler;

import com.project.project_portal.dto.UserQuestProgress;
import com.project.project_portal.service.UserQuestProgressService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class UserQuestProgressHandler {

    private final UserQuestProgressService service;

    public UserQuestProgressHandler(UserQuestProgressService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getProgressForUser(ServerRequest request) {
        String userId = request.pathVariable("userId");
        Flux<UserQuestProgress> progress = service.getByUser(userId);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(progress, UserQuestProgress.class);
    }

    public Mono<ServerResponse> getProgressForUserAndQuest(ServerRequest request) {
        String userId = request.pathVariable("userId");
        String questId = request.pathVariable("questId");
        return service.getByUserAndQuest(userId, questId)
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> upsertProgress(ServerRequest request) {
        Mono<UserQuestProgress> body = request.bodyToMono(UserQuestProgress.class);
        return body
                .flatMap(service::save)
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }
}