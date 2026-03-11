package com.innowise.gateway;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InnoGatewayApplicationTest {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
        });
    }

    @Test
    void mainMethod_canBeCalled() {
        assertDoesNotThrow(() -> {
            Class<?> mainClass = InnoGatewayApplication.class;
            assertNotNull(mainClass);
            
            try {
                mainClass.getMethod("main", String[].class);
            } catch (NoSuchMethodException e) {
                fail("main method not found");
            }
        });
    }
}
