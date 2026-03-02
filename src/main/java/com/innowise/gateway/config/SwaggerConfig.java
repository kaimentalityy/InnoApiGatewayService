package com.innowise.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Value("${server.port:8080}")
        private int serverPort;

        /**
         * Browser-facing Keycloak token URL. Must use localhost (not the internal
         * Docker hostname) because Swagger UI runs in the browser, not inside Docker.
         * Injected via SWAGGER_KEYCLOAK_TOKEN_URL env var in Docker Compose.
         */
        @Value("${swagger.keycloak.token-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/token}")
        private String swaggerKeycloakTokenUrl;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .servers(List.of(new Server().url("http://localhost:" + serverPort)))
                                .info(new Info()
                                                .title("API Gateway Documentation")
                                                .version("1.0")
                                                .description("Documentation for all services behind the API Gateway"))
                                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                                .components(new Components()
                                                .addSecuritySchemes("keycloak",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.OAUTH2)
                                                                                .description("Authenticate via Keycloak (Resource Owner Password Credentials flow)")
                                                                                .flows(new OAuthFlows()
                                                                                                .password(new OAuthFlow()
                                                                                                                .tokenUrl(swaggerKeycloakTokenUrl)
                                                                                                                .scopes(new Scopes()
                                                                                                                                .addString("openid",
                                                                                                                                                "OpenID Connect scope")
                                                                                                                                .addString("profile",
                                                                                                                                                "User profile scope")
                                                                                                                                .addString("email",
                                                                                                                                                "User email scope"))))));
        }
}
