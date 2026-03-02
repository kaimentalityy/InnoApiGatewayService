package com.innowise.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GatewaySecurityConfigTest {

    private final GatewaySecurityConfig config = new GatewaySecurityConfig();

    @Test
    void corsConfigurationSource_allowsAllOriginPatterns() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockServerHttpRequest request = MockServerHttpRequest
                .options("/api/users/register")
                .header("Origin", "http://localhost:5173")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CorsConfiguration corsConfig = source.getCorsConfiguration(exchange);

        assertNotNull(corsConfig);
        assertTrue(corsConfig.getAllowedOriginPatterns().contains("*"));
    }

    @Test
    void corsConfigurationSource_allowsRequiredHttpMethods() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockServerHttpRequest request = MockServerHttpRequest
                .options("/api/orders")
                .header("Origin", "http://localhost:5173")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CorsConfiguration corsConfig = source.getCorsConfiguration(exchange);

        assertNotNull(corsConfig);
        List<String> methods = corsConfig.getAllowedMethods();
        assertTrue(methods.contains(HttpMethod.GET.name()));
        assertTrue(methods.contains(HttpMethod.POST.name()));
        assertTrue(methods.contains(HttpMethod.PUT.name()));
        assertTrue(methods.contains(HttpMethod.DELETE.name()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.name()));
    }

    @Test
    void corsConfigurationSource_allowsCredentials() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/payments")
                .header("Origin", "http://localhost:5173")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CorsConfiguration corsConfig = source.getCorsConfiguration(exchange);

        assertNotNull(corsConfig);
        assertTrue(Boolean.TRUE.equals(corsConfig.getAllowCredentials()));
    }

    @Test
    void corsConfigurationSource_exposesAuthorizationHeader() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/users")
                .header("Origin", "http://localhost:5173")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CorsConfiguration corsConfig = source.getCorsConfiguration(exchange);

        assertNotNull(corsConfig);
        assertTrue(corsConfig.getExposedHeaders().contains("Authorization"));
        assertTrue(corsConfig.getExposedHeaders().contains("X-User-Id"));
    }

    @Test
    void corsConfigurationSource_maxAgeIsSet() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/users")
                .header("Origin", "http://localhost:5173")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CorsConfiguration corsConfig = source.getCorsConfiguration(exchange);

        assertNotNull(corsConfig);
        assertEquals(3600L, corsConfig.getMaxAge());
    }
}
