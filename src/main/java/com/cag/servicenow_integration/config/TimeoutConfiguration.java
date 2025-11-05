package com.cag.servicenow_integration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Timeout configuration for managing API and database timeouts.
 * This helps prevent data overload and ensures responsive API behavior.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.timeout")
public class TimeoutConfiguration {
    
    /**
     * API-level timeout settings
     */
    private ApiTimeout api = new ApiTimeout();
    
    /**
     * Database query timeout settings
     */
    private DatabaseTimeout database = new DatabaseTimeout();
    
    /**
     * Circuit breaker settings for handling timeouts gracefully
     */
    private CircuitBreakerSettings circuitBreaker = new CircuitBreakerSettings();
    
    @Data
    public static class ApiTimeout {
        /**
         * Maximum time (in seconds) the server waits before aborting a request
         * Default: 30 seconds
         */
        private Integer requestTimeoutSeconds = 30;
        
        /**
         * Connection timeout (in milliseconds) for external API calls
         * Default: 5 seconds
         */
        private Integer connectionTimeoutMs = 5000;
        
        /**
         * Read timeout (in milliseconds) for external API calls
         * Default: 10 seconds
         */
        private Integer readTimeoutMs = 10000;
        
        /**
         * Write timeout (in milliseconds) for external API calls
         * Default: 10 seconds
         */
        private Integer writeTimeoutMs = 10000;
        
        /**
         * Maximum time (in seconds) for paginated requests
         * Paginated requests may take longer due to multiple queries
         * Default: 45 seconds
         */
        private Integer paginatedRequestTimeoutSeconds = 45;
        
        /**
         * Warning threshold (in seconds) - log warning if request takes longer
         * Default: 20 seconds
         */
        private Integer warningThresholdSeconds = 20;
    }
    
    @Data
    public static class DatabaseTimeout {
        /**
         * Maximum duration (in seconds) for a database query
         * Default: 10 seconds
         */
        private Integer queryTimeoutSeconds = 10;
        
        /**
         * Maximum duration (in seconds) for paginated database queries
         * Default: 15 seconds
         */
        private Integer paginatedQueryTimeoutSeconds = 15;
        
        /**
         * Connection pool timeout (in milliseconds)
         * Default: 30 seconds
         */
        private Integer connectionPoolTimeoutMs = 30000;
        
        /**
         * Idle timeout (in milliseconds) for database connections
         * Default: 10 minutes
         */
        private Integer idleTimeoutMs = 600000;
        
        /**
         * Maximum lifetime (in milliseconds) for a database connection
         * Default: 30 minutes
         */
        private Integer maxLifetimeMs = 1800000;
    }
    
    @Data
    public static class CircuitBreakerSettings {
        /**
         * Enable circuit breaker for timeout protection
         * Default: true
         */
        private Boolean enabled = true;
        
        /**
         * Number of failures before opening the circuit
         * Default: 3
         */
        private Integer failureThreshold = 3;
        
        /**
         * Time (in seconds) to wait before attempting to close the circuit
         * Default: 30 seconds
         */
        private Integer waitDurationSeconds = 30;
        
        /**
         * Sliding window size for failure rate calculation
         * Default: 10
         */
        private Integer slidingWindowSize = 10;
        
        /**
         * Minimum number of calls required before calculating failure rate
         * Default: 5
         */
        private Integer minimumNumberOfCalls = 5;
    }
    
    /**
     * Check if a processing time exceeds the warning threshold
     */
    public boolean exceedsWarningThreshold(long processingTimeMs) {
        return processingTimeMs > (api.getWarningThresholdSeconds() * 1000L);
    }
    
    /**
     * Get appropriate timeout for request type
     */
    public int getTimeoutForRequest(boolean isPaginated) {
        return isPaginated ? 
            api.getPaginatedRequestTimeoutSeconds() : 
            api.getRequestTimeoutSeconds();
    }
}
