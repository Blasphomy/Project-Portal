package com.project.project_portal.router;

import com.project.project_portal.handler.TaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TaskRouter {

    @Bean
    public RouterFunction<ServerResponse> taskRoutes(TaskHandler handler) {
        return route(GET("/api/tasks"), handler::getAllTasks)
                .andRoute(GET("/api/tasks/{id}"), handler::getTaskById)
                .andRoute(GET("/api/quests/{questId}/tasks"), handler::getTasksByQuestId)
                .andRoute(POST("/api/tasks"), handler::createTask)
                .andRoute(PUT("/api/tasks/{id}"), handler::updateTask)
                .andRoute(DELETE("/api/tasks/{id}"), handler::deleteTask);
    }
}