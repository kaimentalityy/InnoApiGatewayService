package com.innowise.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
        ReflectionTestUtils.setField(swaggerConfig, "serverPort", 8080);
        ReflectionTestUtils.setField(swaggerConfig, "swaggerKeycloakAuthUrl",
                "http://localhost:8088/realms/innowise-realm/protocol/openid-connect/auth");
        ReflectionTestUtils.setField(swaggerConfig, "swaggerKeycloakTokenUrl",
                "http://localhost:8088/realms/innowise-realm/protocol/openid-connect/token");
    }

    @Test
    void customOpenAPI_createsOpenAPIWithCorrectConfiguration() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI);

        List<io.swagger.v3.oas.models.servers.Server> servers = openAPI.getServers();
        assertNotNull(servers);
        assertFalse(servers.isEmpty());
        assertEquals("http://localhost:8080", servers.get(0).getUrl());

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("API Gateway Documentation", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("Documentation for all services behind the API Gateway", info.getDescription());

        List<SecurityRequirement> securityRequirements = openAPI.getSecurity();
        assertNotNull(securityRequirements);
        assertFalse(securityRequirements.isEmpty());
        assertTrue(securityRequirements.get(0).containsKey("keycloak"));

        SecurityScheme keycloakScheme = openAPI.getComponents().getSecuritySchemes().get("keycloak");
        assertNotNull(keycloakScheme);
        assertEquals(SecurityScheme.Type.OAUTH2, keycloakScheme.getType());
        assertEquals("Authenticate via Keycloak (Authorization Code flow)", keycloakScheme.getDescription());

        assertNotNull(keycloakScheme.getFlows().getAuthorizationCode());
        assertEquals("http://localhost:8088/realms/innowise-realm/protocol/openid-connect/auth",
                keycloakScheme.getFlows().getAuthorizationCode().getAuthorizationUrl());
        assertEquals("http://localhost:8088/realms/innowise-realm/protocol/openid-connect/token",
                keycloakScheme.getFlows().getAuthorizationCode().getTokenUrl());

        assertNotNull(keycloakScheme.getFlows().getAuthorizationCode().getScopes());
        assertTrue(keycloakScheme.getFlows().getAuthorizationCode().getScopes().containsKey("openid"));
        assertTrue(keycloakScheme.getFlows().getAuthorizationCode().getScopes().containsKey("profile"));
        assertTrue(keycloakScheme.getFlows().getAuthorizationCode().getScopes().containsKey("email"));
    }

    @Test
    void customOpenAPI_usesDefaultPortWhenNotSet() {
        SwaggerConfig configWithDefaults = new SwaggerConfig();
        ReflectionTestUtils.setField(configWithDefaults, "serverPort", 8080);

        OpenAPI openAPI = configWithDefaults.customOpenAPI();

        assertNotNull(openAPI);
        List<io.swagger.v3.oas.models.servers.Server> servers = openAPI.getServers();
        assertNotNull(servers);
        assertFalse(servers.isEmpty());
        assertEquals("http://localhost:8080", servers.get(0).getUrl());
    }

    @Test
    void customOpenAPI_usesCustomKeycloakUrls() {
        ReflectionTestUtils.setField(swaggerConfig, "swaggerKeycloakAuthUrl", "http://custom-auth:9000/auth");
        ReflectionTestUtils.setField(swaggerConfig, "swaggerKeycloakTokenUrl", "http://custom-auth:9000/token");

        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        SecurityScheme keycloakScheme = openAPI.getComponents().getSecuritySchemes().get("keycloak");
        assertEquals("http://custom-auth:9000/auth",
                keycloakScheme.getFlows().getAuthorizationCode().getAuthorizationUrl());
        assertEquals("http://custom-auth:9000/token",
                keycloakScheme.getFlows().getAuthorizationCode().getTokenUrl());
    }
}
