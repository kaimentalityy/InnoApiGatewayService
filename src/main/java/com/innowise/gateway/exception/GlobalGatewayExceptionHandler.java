package com.innowise.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for the API Gateway.
 * <p>
 * Captures and logs errors from downstream services and converts them into
 * standardized JSON responses with an appropriate HTTP status.
 */
@Slf4j
@Component
public class GlobalGatewayExceptionHandler implements GlobalFilter, Ordered {

    /**
     * Filters and handles exceptions from downstream services.
     *
     * @param exchange the current server exchange
     * @param chain    the filter chain
     * @return a {@link Mono} signaling when handling is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(throwable -> {
                    ServerHttpResponse response = exchange.getResponse();
                    log.error("Exception from downstream service: {}", throwable.getMessage(), throwable);

                    if (!response.isCommitted()) {
                        response.setStatusCode(HttpStatus.BAD_GATEWAY);
                        byte[] bytes = ("{\"error\": \"" + throwable.getMessage() + "\"}").getBytes();
                        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                    }

                    return Mono.error(throwable);
                });
    }

    /**
     * Defines the order of this global exception filter.
     *
     * @return the order value; lower values are executed earlier
     */
    @Override
    public int getOrder() {
        return -2;
    }
}
