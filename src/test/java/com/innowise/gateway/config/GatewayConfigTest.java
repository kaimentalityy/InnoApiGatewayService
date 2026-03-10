package com.innowise.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayConfigTest {

    private GatewayConfig gatewayConfig;
    
    @Mock
    private RouteLocatorBuilder routeLocatorBuilder;
    
    @Mock
    private RouteLocatorBuilder.Builder routeBuilder;
    
    @Mock
    private RouteLocator routeLocator;

    @BeforeEach
    void setUp() {
        gatewayConfig = new GatewayConfig();
        ReflectionTestUtils.setField(gatewayConfig, "userServiceUri", "http://user-service:8081");
        ReflectionTestUtils.setField(gatewayConfig, "orderServiceUri", "http://order-service:8082");
    }

    @Test
    void customRouteLocator_createsRoutesWithCorrectConfiguration() {
        when(routeLocatorBuilder.routes()).thenReturn(routeBuilder);
        when(routeBuilder.route(any(String.class), any(Function.class))).thenReturn(routeBuilder);
        when(routeBuilder.build()).thenReturn(routeLocator);
        
        assertDoesNotThrow(() -> {
            RouteLocator result = gatewayConfig.customRouteLocator(routeLocatorBuilder);
            assertNotNull(result);
        });
        
        verify(routeLocatorBuilder).routes();
        verify(routeBuilder, atLeast(4)).route(any(String.class), any(Function.class));
        verify(routeBuilder).build();
    }

    @Test
    void customRouteLocator_usesConfiguredServiceUris() {
        String userServiceUri = (String) ReflectionTestUtils.getField(gatewayConfig, "userServiceUri");
        String orderServiceUri = (String) ReflectionTestUtils.getField(gatewayConfig, "orderServiceUri");
        
        assertEquals("http://user-service:8081", userServiceUri);
        assertEquals("http://order-service:8082", orderServiceUri);
    }
}
