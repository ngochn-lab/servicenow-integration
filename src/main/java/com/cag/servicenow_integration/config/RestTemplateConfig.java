package com.cag.servicenow_integration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * RestTemplate configuration with proper timeout settings to prevent data overload.
 * This configuration ensures all HTTP clients respect the defined timeout limits.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    
    private final TimeoutConfiguration timeoutConfiguration;
    
    /**
     * Primary RestTemplate bean with standard timeout configuration.
     * Used for regular API calls.
     */
    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Configuring RestTemplate with timeout settings: connection={}ms, read={}ms",
                timeoutConfiguration.getApi().getConnectionTimeoutMs(),
                timeoutConfiguration.getApi().getReadTimeoutMs());

        return builder
                .connectTimeout(Duration.ofMillis(timeoutConfiguration.getApi().getConnectionTimeoutMs()))
                .readTimeout(Duration.ofMillis(timeoutConfiguration.getApi().getReadTimeoutMs()))
                .requestFactory(this::createRequestFactory)
                .interceptors(createInterceptors())
                .build();
    }
    
    /**
     * RestTemplate specifically for paginated requests with extended timeouts.
     * Use this for endpoints that return large datasets.
     */
    @Bean(name = "paginatedRestTemplate")
    public RestTemplate paginatedRestTemplate(RestTemplateBuilder builder) {
        log.info("Configuring paginated RestTemplate with extended timeout: {}s", 
                timeoutConfiguration.getApi().getPaginatedRequestTimeoutSeconds());
        
        // Use longer timeouts for paginated requests
        long paginatedTimeoutMs = timeoutConfiguration.getApi().getPaginatedRequestTimeoutSeconds() * 1000L;
        
        return builder
                .connectTimeout(Duration.ofMillis(timeoutConfiguration.getApi().getConnectionTimeoutMs()))
                .readTimeout(Duration.ofMillis(paginatedTimeoutMs))
                .requestFactory(this::createPaginatedRequestFactory)
                .interceptors(createInterceptors())
                .build();
    }
    
    /**
     * Create request factory with standard timeout settings.
     */
    private ClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutConfiguration.getApi().getConnectionTimeoutMs());
        factory.setReadTimeout(timeoutConfiguration.getApi().getReadTimeoutMs());
        
        // Enable buffering for request/response logging
        return new BufferingClientHttpRequestFactory(factory);
    }
    
    /**
     * Create request factory with extended timeout for paginated requests.
     */
    private ClientHttpRequestFactory createPaginatedRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutConfiguration.getApi().getConnectionTimeoutMs());
        
        // Use extended timeout for paginated requests
        int paginatedTimeoutMs = timeoutConfiguration.getApi().getPaginatedRequestTimeoutSeconds() * 1000;
        factory.setReadTimeout(paginatedTimeoutMs);
        
        // Enable buffering for request/response logging
        return new BufferingClientHttpRequestFactory(factory);
    }
    
    /**
     * Create interceptors for logging and monitoring.
     */
    private List<ClientHttpRequestInterceptor> createInterceptors() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        
        // Add timing interceptor
        interceptors.add((request, body, execution) -> {
            long startTime = System.currentTimeMillis();
            var response = execution.execute(request, body);
            long duration = System.currentTimeMillis() - startTime;
            
            // Log warning if request exceeds threshold
            if (timeoutConfiguration.exceedsWarningThreshold(duration)) {
                log.warn("Request to {} took {}ms, exceeding warning threshold of {}s",
                        request.getURI(), duration, 
                        timeoutConfiguration.getApi().getWarningThresholdSeconds());
            } else {
                log.debug("Request to {} completed in {}ms", request.getURI(), duration);
            }
            
            return response;
        });
        
        return interceptors;
    }
}
