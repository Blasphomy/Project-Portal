package com.project.project_portal.router;

import com.project.project_portal.handler.HelloHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HelloRouter {
    @Bean
    public RouterFunction<ServerResponse> route(HelloHandler handler) {
        return RouterFunctions.route()
                .GET("/hello", handler::sayHello)
                .build();
    }
}
