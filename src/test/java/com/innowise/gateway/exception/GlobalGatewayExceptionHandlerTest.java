package com.innowise.gateway.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalGatewayExceptionHandlerTest {

    private GlobalGatewayExceptionHandler handler;

    @Mock
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        handler = new GlobalGatewayExceptionHandler();
    }

    @Test
    void filter_noError_passesThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(handler.filter(exchange, chain))
                .verifyComplete();

        // Response should not be manually set when no error occurs
        assertNull(exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_downstreamThrows_returns502WithMessage() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        StepVerifier.create(handler.filter(exchange, chain))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_GATEWAY, exchange.getResponse().getStatusCode());
    }

    @Test
    void filter_committedResponse_rethrowsError() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/payments").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        // Commit the response so isCommitted() returns true
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().writeWith(Mono.empty()).subscribe();

        RuntimeException cause = new RuntimeException("already committed");
        when(chain.filter(any())).thenReturn(Mono.error(cause));

        StepVerifier.create(handler.filter(exchange, chain))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getOrder_returnsMinusTwo() {
        assertEquals(-2, handler.getOrder());
    }
}
