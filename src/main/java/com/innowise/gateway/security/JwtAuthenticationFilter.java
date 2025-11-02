package com.innowise.gateway.security;

import com.innowise.gateway.util.JwtValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global JWT authentication filter for the API Gateway.
 * <p>
 * Intercepts incoming requests and validates JWT tokens using the {@link JwtValidator}.
 * Skips authentication for public endpoints like login, registration, and token refresh.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;

    /**
     * Constructs the JWT authentication filter.
     *
     * @param jwtValidator the validator used to verify JWT tokens
     */
    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    /**
     * Filters incoming HTTP requests and validates their JWT tokens.
     *
     * @param exchange the current server exchange
     * @param chain    the filter chain
     * @return a reactive {@link Mono} that completes when the filtering is done
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        return jwtValidator.validate(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    /**
     * Defines the order of this filter relative to others.
     *
     * @return the order value; lower values have higher precedence
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
