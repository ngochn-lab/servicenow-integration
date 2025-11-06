package com.cag.servicenow_integration.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sap.api")
@Getter
public class SAPApiProperties {
    private String baseUrl = "http://localhost:8088";
    private String clientId;
    private String clientSecret;
}

