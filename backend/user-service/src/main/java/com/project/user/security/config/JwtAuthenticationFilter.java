package com.project.user.security.config;

import com.project.user.service.JwtService;
import com.project.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            return chain.filter(exchange);
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(userEmail)) {
            return userService.reactiveUserDetailsService().findByUsername(userEmail)
                    .filter(userDetails -> jwtService.isTokenValid(jwt, userDetails))
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                    });
        }
        return chain.filter(exchange);
    }
}
