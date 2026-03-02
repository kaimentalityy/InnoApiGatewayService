package com.innowise.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthHeaderForwardingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getPath().toString();
        
        log.info("Forwarding request to {} with Authorization header present: {}", path, authHeader != null);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            exchange = exchange.mutate()
                    .request(r -> r.header(HttpHeaders.AUTHORIZATION, authHeader))
                    .build();
            log.debug("Forwarding Authorization header: {}...", authHeader.substring(0, Math.min(20, authHeader.length())));
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
