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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

        @Value("${server.port:8080}")
        private int serverPort;

        /**
         * Browser-facing Keycloak authorization URL.
         */
        @Value("${swagger.keycloak.auth-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/auth}")
        private String swaggerKeycloakAuthUrl;

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
                                                                                .description("Authenticate via Keycloak (Authorization Code flow)")
                                                                                .flows(new OAuthFlows()
                                                                                                .authorizationCode(
                                                                                                                new OAuthFlow()
                                                                                                                                .authorizationUrl(
                                                                                                                                                swaggerKeycloakAuthUrl)
                                                                                                                                .tokenUrl(swaggerKeycloakTokenUrl)
                                                                                                                                .scopes(new Scopes()
                                                                                                                                                .addString("openid",
                                                                                                                                                                "OpenID Connect scope")
                                                                                                                                                .addString("profile",
                                                                                                                                                                "User profile scope")
                                                                                                                                                .addString("email",
                                                                                                                                                                "User email scope"))))));
        }

        @Primary
        @Bean
        public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties properties) {
                SwaggerUiConfigParameters parameters = new SwaggerUiConfigParameters(properties);
                Map<String, Object> config = parameters.getConfigParameters();
                config.put("usePkceWithAuthorizationCodeGrant", true);
                config.put("clientId", "innowise-client");
                config.put("clientSecret", "innowise-secret");
                return parameters;
        }
}
