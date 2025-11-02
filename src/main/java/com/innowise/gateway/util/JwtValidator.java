package com.innowise.gateway.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Utility component for validating JWT tokens through the Authentication Service.
 * <p>
 * Sends validation requests to the auth-service and interprets the response.
 */
@Component
public class JwtValidator {

    private final WebClient webClient;

    /**
     * Constructs the JWT validator with a configured {@link WebClient}.
     *
     * @param webClientBuilder the builder for creating {@link WebClient} instances
     * @param authUrl          the base URL of the authentication service
     */
    public JwtValidator(WebClient.Builder webClientBuilder, @Value("${spring.cloud.gateway.routes[0].uri}") String authUrl) {
        this.webClient = webClientBuilder.baseUrl(authUrl).build();
    }

    /**
     * Validates the given JWT token by delegating the check to the authentication service.
     *
     * @param token the JWT token to validate
     * @return a {@link Mono} emitting {@code true} if the token is valid, or {@code false} otherwise
     */
    public Mono<Boolean> validate(String token) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/auth/validate")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (Boolean) map.getOrDefault("valid", false))
                .onErrorReturn(false);
    }
}
