package com.project.project_portal.router;

import com.project.project_portal.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route(GET("/api/users"), handler::getAllUsers)
                .andRoute(GET("/api/users/{id}"), handler::getUserById)
                .andRoute(POST("/api/users"), handler::createUser)
                .andRoute(PUT("/api/users/{id}"), handler::updateUser)
                .andRoute(DELETE("/api/users/{id}"), handler::deleteUser);
    }
}