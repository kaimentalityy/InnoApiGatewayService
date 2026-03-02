package com.innowise.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    private LoggingFilter filter;

    @Mock
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new LoggingFilter();
    }

    @Test
    void filter_logsRequestAndResponse() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_postRequest_logsCorrectly() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/users/register").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void getOrder_returnsHighestPrecedence() {
        assertEquals(Ordered.HIGHEST_PRECEDENCE, filter.getOrder());
    }
}
