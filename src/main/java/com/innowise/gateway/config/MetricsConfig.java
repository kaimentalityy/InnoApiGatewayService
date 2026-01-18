package com.innowise.gateway.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for custom business metrics using Micrometer.
 * These metrics will be exposed via Prometheus and visualized in Grafana.
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    /**
     * Customizes the MeterRegistry to add common tags to all metrics.
     * These tags help identify metrics in Prometheus/Grafana.
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "inno-api-gateway",
                        "service", "api-gateway");
    }

    /**
     * Enables @Timed annotation support for method execution timing.
     * This allows precise timing of specific methods.
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Counter for tracking total gateway requests
     */
    @Bean
    public Counter gatewayRequestsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.requests.total")
                .description("Total number of requests through the gateway")
                .tag("service", "api-gateway")
                .register(registry);
    }

    /**
     * Counter for tracking successful gateway responses
     */
    @Bean
    public Counter gatewaySuccessCounter(MeterRegistry registry) {
        return Counter.builder("gateway.responses.success")
                .description("Number of successful gateway responses")
                .tag("service", "api-gateway")
                .tag("status", "success")
                .register(registry);
    }

    /**
     * Counter for tracking failed gateway responses
     */
    @Bean
    public Counter gatewayFailureCounter(MeterRegistry registry) {
        return Counter.builder("gateway.responses.failure")
                .description("Number of failed gateway responses")
                .tag("service", "api-gateway")
                .tag("status", "failure")
                .register(registry);
    }

    /**
     * Timer for tracking request routing duration
     */
    @Bean
    public Timer routingTimer(MeterRegistry registry) {
        return Timer.builder("gateway.routing.duration")
                .description("Time taken to route requests")
                .tag("service", "api-gateway")
                .register(registry);
    }

    /**
     * Counter for tracking JWT validation attempts
     */
    @Bean
    public Counter jwtValidationAttemptsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.jwt.validation.attempts")
                .description("Total number of JWT validation attempts")
                .tag("service", "api-gateway")
                .register(registry);
    }

    /**
     * Counter for tracking successful JWT validations
     */
    @Bean
    public Counter jwtValidationSuccessCounter(MeterRegistry registry) {
        return Counter.builder("gateway.jwt.validation.success")
                .description("Number of successful JWT validations")
                .tag("service", "api-gateway")
                .register(registry);
    }

    /**
     * Counter for tracking failed JWT validations
     */
    @Bean
    public Counter jwtValidationFailureCounter(MeterRegistry registry) {
        return Counter.builder("gateway.jwt.validation.failure")
                .description("Number of failed JWT validations")
                .tag("service", "api-gateway")
                .register(registry);
    }

    /**
     * Counter for tracking requests by service
     */
    @Bean
    public Counter authServiceRequestsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.service.requests")
                .description("Number of requests routed to Auth Service")
                .tag("service", "api-gateway")
                .tag("target", "auth-service")
                .register(registry);
    }

    @Bean
    public Counter userServiceRequestsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.service.requests")
                .description("Number of requests routed to User Service")
                .tag("service", "api-gateway")
                .tag("target", "user-service")
                .register(registry);
    }

    @Bean
    public Counter orderServiceRequestsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.service.requests")
                .description("Number of requests routed to Order Service")
                .tag("service", "api-gateway")
                .tag("target", "order-service")
                .register(registry);
    }

    @Bean
    public Counter paymentServiceRequestsCounter(MeterRegistry registry) {
        return Counter.builder("gateway.service.requests")
                .description("Number of requests routed to Payment Service")
                .tag("service", "api-gateway")
                .tag("target", "payment-service")
                .register(registry);
    }
}
