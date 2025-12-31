package com.project.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Filter for API Gateway
 * Limits requests per IP address
 */
@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private final ConcurrentHashMap<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();

    public static class Config {
        private int maxRequests = 100; // Max requests per window
        private Duration window = Duration.ofMinutes(1); // Time window

        public int getMaxRequests() {
            return maxRequests;
        }

        public void setMaxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
        }

        public Duration getWindow() {
            return window;
        }

        public void setWindow(Duration window) {
            this.window = window;
        }
    }

    public RateLimitFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

            RateLimitInfo rateLimitInfo = requestCounts.computeIfAbsent(
                    clientIp,
                    k -> new RateLimitInfo(config.getMaxRequests(), config.getWindow()));

            if (rateLimitInfo.tryAcquire()) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After",
                        String.valueOf(rateLimitInfo.getResetTime()));
                return exchange.getResponse().setComplete();
            }
        };
    }

    private static class RateLimitInfo {
        private final AtomicInteger counter;
        private long resetTime;
        private final int maxRequests;
        private final long windowMillis;

        public RateLimitInfo(int maxRequests, Duration window) {
            this.maxRequests = maxRequests;
            this.windowMillis = window.toMillis();
            this.counter = new AtomicInteger(0);
            this.resetTime = System.currentTimeMillis() + windowMillis;
        }

        public synchronized boolean tryAcquire() {
            long currentTime = System.currentTimeMillis();

            // Reset if window expired
            if (currentTime > resetTime) {
                counter.set(0);
                resetTime = currentTime + windowMillis;
            }

            int currentCount = counter.incrementAndGet();
            return currentCount <= maxRequests;
        }

        public long getResetTime() {
            return (resetTime - System.currentTimeMillis()) / 1000;
        }
    }
}
