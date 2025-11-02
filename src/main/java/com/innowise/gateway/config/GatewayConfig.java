package com.innowise.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${AUTH_SERVICE_URI}")
    private String authServiceUri;

    @Value("${USER_SERVICE_URI}")
    private String userServiceUri;

    @Value("${ORDER_SERVICE_URI}")
    private String orderServiceUri;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", r -> r.path("/api/orders/**", "/api/items/**", "/api/order-items/**")
                        .uri(orderServiceUri))
                .route("user-service", r -> r.path("/api/users/**", "/api/cards/**")
                        .uri(userServiceUri))
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri(authServiceUri))
                .build();
    }
}
