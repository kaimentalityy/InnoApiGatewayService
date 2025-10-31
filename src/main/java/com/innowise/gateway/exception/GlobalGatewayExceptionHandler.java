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

@Slf4j
@Component
public class GlobalGatewayExceptionHandler implements GlobalFilter, Ordered {

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

    @Override
    public int getOrder() {
        return -2;
    }
}
