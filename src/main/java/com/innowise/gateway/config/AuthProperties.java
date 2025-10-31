package com.innowise.gateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AuthProperties {

    @Value("${TOKEN_PREFIX}")
    private String tokenPrefix;

    @Value("${HEADER_USER_ID}")
    private String headerUserId;

    @Value("${CONTENT_TYPE_JSON}")
    private String contentTypeJson;
}
