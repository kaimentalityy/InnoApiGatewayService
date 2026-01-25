package com.innowise.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalMetricsFilter implements GlobalFilter, Ordered {

    private final Counter gatewayRequestsCounter;
    private final Counter gatewaySuccessCounter;
    private final Counter gatewayFailureCounter;
    private final Timer routingTimer;

    private final Counter authServiceRequestsCounter;
    private final Counter userServiceRequestsCounter;
    private final Counter orderServiceRequestsCounter;
    private final Counter paymentServiceRequestsCounter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.nanoTime();
        gatewayRequestsCounter.increment();

        String path = exchange.getRequest().getPath().toString();
        incrementServiceCounter(path);

        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    long duration = System.nanoTime() - startTime;
                    routingTimer.record(duration, TimeUnit.NANOSECONDS);

                    if (exchange.getResponse().getStatusCode() != null &&
                            exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                        gatewaySuccessCounter.increment();
                    } else {
                        gatewayFailureCounter.increment();
                    }
                })
                .doOnError(e -> {
                    gatewayFailureCounter.increment();
                });
    }

    private void incrementServiceCounter(String path) {
        if (path.contains("/api/auth")) {
            authServiceRequestsCounter.increment();
        } else if (path.contains("/api/users") || path.contains("/api/cards")) {
            userServiceRequestsCounter.increment();
        } else if (path.contains("/api/orders") || path.contains("/api/items")) {
            orderServiceRequestsCounter.increment();
        } else if (path.contains("/api/payments")) {
            paymentServiceRequestsCounter.increment();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
