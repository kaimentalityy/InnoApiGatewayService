package com.innowise.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalMetricsFilterTest {

    @Mock
    private Counter gatewayRequestsCounter;
    @Mock
    private Counter gatewaySuccessCounter;
    @Mock
    private Counter gatewayFailureCounter;
    @Mock
    private Timer routingTimer;
    @Mock
    private Counter authServiceRequestsCounter;
    @Mock
    private Counter userServiceRequestsCounter;
    @Mock
    private Counter orderServiceRequestsCounter;
    @Mock
    private Counter paymentServiceRequestsCounter;
    @Mock
    private GatewayFilterChain chain;

    private GlobalMetricsFilter filter;

    @BeforeEach
    void setUp() {
        filter = new GlobalMetricsFilter(
                gatewayRequestsCounter, gatewaySuccessCounter, gatewayFailureCounter,
                routingTimer,
                authServiceRequestsCounter, userServiceRequestsCounter,
                orderServiceRequestsCounter, paymentServiceRequestsCounter);
    }

    @Test
    void filter_successfulRequest_incrementsSuccessCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/me").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(chain.filter(any())).thenReturn(Mono.empty());
        doNothing().when(routingTimer).record(anyLong(), any(TimeUnit.class));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(gatewayRequestsCounter).increment();
        verify(userServiceRequestsCounter).increment();
        verify(gatewaySuccessCounter).increment();
    }

    @Test
    void filter_nonSuccessStatus_incrementsFailureCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);

        when(chain.filter(any())).thenReturn(Mono.empty());
        doNothing().when(routingTimer).record(anyLong(), any(TimeUnit.class));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(orderServiceRequestsCounter).increment();
        verify(gatewayFailureCounter).increment();
    }

    @Test
    void filter_errorFromChain_incrementsFailureCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/payments/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.error(new RuntimeException("downstream failure")));

        StepVerifier.create(filter.filter(exchange, chain))
                .expectError(RuntimeException.class)
                .verify();

        verify(paymentServiceRequestsCounter).increment();
        verify(gatewayFailureCounter).increment();
    }

    @Test
    void filter_authPath_incrementsAuthCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/token").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(chain.filter(any())).thenReturn(Mono.empty());
        doNothing().when(routingTimer).record(anyLong(), any(TimeUnit.class));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(authServiceRequestsCounter).increment();
    }

    @Test
    void filter_cardsPath_incrementsUserServiceCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/cards/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(chain.filter(any())).thenReturn(Mono.empty());
        doNothing().when(routingTimer).record(anyLong(), any(TimeUnit.class));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(userServiceRequestsCounter).increment();
    }

    @Test
    void filter_unknownPath_doesNotIncrementServiceCounter() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(chain.filter(any())).thenReturn(Mono.empty());
        doNothing().when(routingTimer).record(anyLong(), any(TimeUnit.class));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verifyNoInteractions(authServiceRequestsCounter, userServiceRequestsCounter,
                orderServiceRequestsCounter, paymentServiceRequestsCounter);
    }

    @Test
    void getOrder_returnsHighestPrecedence() {
        assertEquals(Ordered.HIGHEST_PRECEDENCE, filter.getOrder());
    }
}
