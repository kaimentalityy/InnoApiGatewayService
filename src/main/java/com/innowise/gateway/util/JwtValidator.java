package com.innowise.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Utility component for validating JWT tokens locally.
 * <p>
 * Uses a shared secret key to verify the signature and validity of JWT tokens.
 */
@Component
public class JwtValidator {

    private final SecretKey key;
    private final Counter jwtValidationAttemptsCounter;
    private final Counter jwtValidationSuccessCounter;
    private final Counter jwtValidationFailureCounter;

    public JwtValidator(@Value("${JWT_SECRET}") String secret,
            Counter jwtValidationAttemptsCounter,
            Counter jwtValidationSuccessCounter,
            Counter jwtValidationFailureCounter) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtValidationAttemptsCounter = jwtValidationAttemptsCounter;
        this.jwtValidationSuccessCounter = jwtValidationSuccessCounter;
        this.jwtValidationFailureCounter = jwtValidationFailureCounter;
    }

    /**
     * Validates the token and returns the claims if valid.
     *
     * @param token the JWT token
     * @return a {@link Mono} emitting the claims if valid, or error/empty if
     *         invalid
     */
    public Mono<Claims> validateAndGetClaims(String token) {
        return Mono.fromCallable(() -> {
            jwtValidationAttemptsCounter.increment();
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                jwtValidationSuccessCounter.increment();
                return claims;
            } catch (Exception e) {
                jwtValidationFailureCounter.increment();
                throw e;
            }
        });
    }

    /**
     * Validates the given JWT token locally using the configured secret key.
     *
     * @param token the JWT token to validate
     * @return a {@link Mono} emitting {@code true} if the token is valid, or
     *         {@code false} otherwise
     */
    public Mono<Boolean> validate(String token) {
        return validateAndGetClaims(token)
                .map(claims -> true)
                .onErrorResume(e -> Mono.just(false));
    }
}
