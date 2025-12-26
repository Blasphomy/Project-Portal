package com.project.project_portal.filter;


import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();

        logger.info("Incoming request: {} {}", method, path);

        return chain.filter(exchange).doFinally(signal -> {
            long duration = System.currentTimeMillis() - startTime;
            int status = exchange.getResponse().getStatusCode() != null ?
                    exchange.getResponse().getStatusCode().value() : 0;
            logger.info("Response: {} {} - Status: {} - Duration: {}ms",
                    method, path, status, duration);
        });
    }
}
