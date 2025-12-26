package com.project.project_portal.router;

import com.project.project_portal.handler.UserQuestProgressHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserQuestProgressRouter {

    @Bean
    public RouterFunction<ServerResponse> userQuestProgressRoutes(UserQuestProgressHandler handler) {
        return route(GET("/api/users/{userId}/quest-progress"), handler::getProgressForUser)
                .andRoute(GET("/api/users/{userId}/quest-progress/{questId}"), handler::getProgressForUserAndQuest)
                .andRoute(POST("/api/users/quest-progress"), handler::upsertProgress);
    }
}