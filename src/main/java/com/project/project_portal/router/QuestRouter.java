package com.project.project_portal.router;

import com.project.project_portal.handler.QuestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class QuestRouter {

    @Bean
    public RouterFunction<ServerResponse> questRoutes(QuestHandler handler) {
        return route(GET("/api/quests"), handler::getAllQuests)
                .andRoute(GET("/api/quests/{id}"), handler::getQuestById)
                .andRoute(GET("/api/topics/{topicId}/quests"), handler::getQuestsByTopicId)
                .andRoute(POST("/api/quests"), handler::createQuest)
                .andRoute(PUT("/api/quests/{id}"), handler::updateQuest)
                .andRoute(DELETE("/api/quests/{id}"), handler::deleteQuest);
    }
}