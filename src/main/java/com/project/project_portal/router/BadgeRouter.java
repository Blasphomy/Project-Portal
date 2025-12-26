package com.project.project_portal.router;

import com.project.project_portal.handler.BadgeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BadgeRouter {

    @Bean
    public RouterFunction<ServerResponse> badgeRoutes(BadgeHandler handler) {
        return route(GET("/api/badges"), handler::getAllBadges)
                .andRoute(GET("/api/badges/{id}"), handler::getBadgeById)
                .andRoute(POST("/api/badges"), handler::createBadge)
                .andRoute(GET("/api/users/{userId}/badges"), handler::getUserBadges)
                .andRoute(POST("/api/users/{userId}/badges/{badgeId}"), handler::awardBadge);
    }
}