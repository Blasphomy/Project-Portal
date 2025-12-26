package com.project.project_portal.router;

import com.project.project_portal.handler.ProgressHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProgressRouter {

    @Bean
    public RouterFunction<ServerResponse> progressRoutes(ProgressHandler handler) {
        return route(POST("/api/progress/tasks/{taskId}/start"), handler::startTask)
                .andRoute(POST("/api/progress/tasks/{taskId}/complete"), handler::completeTask)
                .andRoute(GET("/api/progress/users/{userId}/tasks/{taskId}"), handler::getUserTaskProgress)
                .andRoute(GET("/api/progress/users/{userId}/quests/{questId}"), handler::getUserQuestProgress)
                .andRoute(GET("/api/progress/users/{userId}/quests"), handler::getAllUserQuestProgress)
                .andRoute(GET("/api/progress/users/{userId}/tasks"), handler::getAllUserTaskProgress)
                .andRoute(GET("/api/progress/users/{userId}/quests/{questId}/with-tasks"), handler::getUserQuestWithTasks)
                .andRoute(GET("/api/progress/users/{userId}/completion-status"), handler::getUserCompletionStatus)
                .andRoute(POST("/api/progress/users/{userId}/award-mastery"), handler::awardMasteryBadges);
    }
}