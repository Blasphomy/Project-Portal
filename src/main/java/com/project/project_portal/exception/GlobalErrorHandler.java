package com.project.project_portal.exception;


import com.project.project_portal.dto.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GlobalErrorHandler provides centralized error handling for the entire application.
 *
 * Catches unhandled exceptions and returns structured error responses.
 * Should be enhanced with proper JSON serialization for production use.
 */
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    /**
     * Handles any unhandled exceptions that occur during request processing.
     *
     * TODO: Improve error response format by properly serializing to JSON
     * Current implementation returns plain text error messages.
     * Consider using Jackson ObjectMapper for proper JSON responses.
     *
     * @param exchange The server web exchange
     * @param ex The exception that occurred
     * @return Mono<Void> representing completion of error handling
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Set response status to 500 Internal Server Error
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        // TODO: Use ObjectMapper to properly serialize ErrorResponse to JSON
        // Current approach: exchange.getResponse().writeWith(...)
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(errorResponse.getMessage().getBytes()))
        );
    }
}
