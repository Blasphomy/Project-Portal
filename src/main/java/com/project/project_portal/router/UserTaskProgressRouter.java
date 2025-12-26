package com.project.project_portal.router;

import com.project.project_portal.handler.UserTaskProgressHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserTaskProgressRouter {

    @Bean
    public RouterFunction<ServerResponse> userTaskProgressRoutes(UserTaskProgressHandler handler) {
        return route(GET("/api/users/{userId}/task-progress"), handler::getProgressForUser)
                .andRoute(GET("/api/users/{userId}/task-progress/{taskId}"), handler::getProgressForUserAndTask)
                .andRoute(POST("/api/users/task-progress"), handler::upsertProgress);
    }
}