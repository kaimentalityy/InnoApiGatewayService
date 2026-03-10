package com.innowise.gateway.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import static org.junit.jupiter.api.Assertions.*;

class MetricsConfigTest {

    private MeterRegistry meterRegistry;

    private MetricsConfig metricsConfig;

    @BeforeEach
    void setUp() {
        metricsConfig = new MetricsConfig();
        meterRegistry = new SimpleMeterRegistry();
    }

    @Test
    void metricsCommonTags_returnsCustomizer() {
        MeterRegistryCustomizer<MeterRegistry> customizer = metricsConfig.metricsCommonTags();

        assertNotNull(customizer);
        assertDoesNotThrow(() -> customizer.customize(meterRegistry));
    }

    @Test
    void timedAspect_createsTimedAspect() {
        assertNotNull(metricsConfig.timedAspect(meterRegistry));
    }

    @Test
    void gatewayRequestsCounter_createsCounter() {
        Counter counter = metricsConfig.gatewayRequestsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.requests.total", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
    }

    @Test
    void gatewaySuccessCounter_createsCounter() {
        Counter counter = metricsConfig.gatewaySuccessCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.responses.success", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("success", counter.getId().getTag("status"));
    }

    @Test
    void gatewayFailureCounter_createsCounter() {
        Counter counter = metricsConfig.gatewayFailureCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.responses.failure", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("failure", counter.getId().getTag("status"));
    }

    @Test
    void routingTimer_createsTimer() {
        Timer timer = metricsConfig.routingTimer(meterRegistry);

        assertNotNull(timer);
        assertEquals("gateway.routing.duration", timer.getId().getName());
        assertEquals("api-gateway", timer.getId().getTag("service"));
    }

    @Test
    void jwtValidationAttemptsCounter_createsCounter() {
        Counter counter = metricsConfig.jwtValidationAttemptsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.jwt.validation.attempts", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
    }

    @Test
    void jwtValidationSuccessCounter_createsCounter() {
        Counter counter = metricsConfig.jwtValidationSuccessCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.jwt.validation.success", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
    }

    @Test
    void jwtValidationFailureCounter_createsCounter() {
        Counter counter = metricsConfig.jwtValidationFailureCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.jwt.validation.failure", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
    }

    @Test
    void authServiceRequestsCounter_createsCounter() {
        Counter counter = metricsConfig.authServiceRequestsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.service.requests", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("auth-service", counter.getId().getTag("target"));
    }

    @Test
    void userServiceRequestsCounter_createsCounter() {
        Counter counter = metricsConfig.userServiceRequestsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.service.requests", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("user-service", counter.getId().getTag("target"));
    }

    @Test
    void orderServiceRequestsCounter_createsCounter() {
        Counter counter = metricsConfig.orderServiceRequestsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.service.requests", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("order-service", counter.getId().getTag("target"));
    }

    @Test
    void paymentServiceRequestsCounter_createsCounter() {
        Counter counter = metricsConfig.paymentServiceRequestsCounter(meterRegistry);

        assertNotNull(counter);
        assertEquals("gateway.service.requests", counter.getId().getName());
        assertEquals("api-gateway", counter.getId().getTag("service"));
        assertEquals("payment-service", counter.getId().getTag("target"));
    }
}
