package com.project.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Global filter for request logging and adding correlation IDs
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = Instant.now().toEpochMilli();

        String correlationId = request.getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = java.util.UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        log.info("Incoming request: {} {} - Correlation-ID: {}",
                request.getMethod(),
                request.getPath(),
                finalCorrelationId);

        // Add correlation ID to request headers
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Correlation-ID", finalCorrelationId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long endTime = Instant.now().toEpochMilli();
                    log.info("Request completed: {} {} - Duration: {}ms - Correlation-ID: {}",
                            request.getMethod(),
                            request.getPath(),
                            (endTime - startTime),
                            finalCorrelationId);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
