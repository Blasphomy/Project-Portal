package com.project.project_portal.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class HelloHandler {
    public Mono<ServerResponse> sayHello(ServerRequest request) {
        System.out.println("Button clicked via WebFlux!");
        return ServerResponse.ok().bodyValue("Hello from WebFlux backend!");
    }
}
