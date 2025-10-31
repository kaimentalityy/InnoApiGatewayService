package com.innowise.gateway.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class JwtValidator {

    private final WebClient webClient;

    public JwtValidator(WebClient.Builder webClientBuilder, @Value("${spring.cloud.gateway.routes[0].uri}") String authUrl) {
        this.webClient = webClientBuilder.baseUrl(authUrl).build();
    }

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

