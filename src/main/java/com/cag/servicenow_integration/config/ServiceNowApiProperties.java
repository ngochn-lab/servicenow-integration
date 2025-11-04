package com.cag.servicenow_integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "servicenow.api")
@Getter
@Setter
public class ServiceNowApiProperties {
    private String instanceBaseUrl;
}
