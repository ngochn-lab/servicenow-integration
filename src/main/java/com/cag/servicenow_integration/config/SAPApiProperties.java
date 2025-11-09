package com.cag.servicenow_integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sap.api")
@Getter
@Setter
public class SAPApiProperties {
    private String baseUrl;
    private String onboardingCandidateEndpoint;
    private String authToken;
}

