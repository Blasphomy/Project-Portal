package com.project.project_portal.router;

import com.project.project_portal.handler.TopicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TopicRouter {

    @Bean
    public RouterFunction<ServerResponse> topicRoutes(TopicHandler handler) {
        return route(GET("/api/topics"), handler::getAllTopics)
                .andRoute(GET("/api/topics/{id}"), handler::getTopicById)
                .andRoute(GET("/api/topics/{id}/tree"), handler::getTopicTree)
                .andRoute(POST("/api/topics"), handler::createTopic)
                .andRoute(PUT("/api/topics/{id}"), handler::updateTopic)
                .andRoute(DELETE("/api/topics/{id}"), handler::deleteTopic);
    }
}