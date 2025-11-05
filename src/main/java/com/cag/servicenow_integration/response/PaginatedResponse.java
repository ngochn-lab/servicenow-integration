package com.cag.servicenow_integration.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated response wrapper with metadata for API consumers.
 * This structure helps consumers manage pagination effectively and prevent data overload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> {
    
    /**
     * The actual data/results for this page
     */
    private List<T> data;
    
    /**
     * Pagination metadata
     */
    private PaginationMetadata pagination;
    
    /**
     * Response status information
     */
    private ResponseStatus status;
    
    /**
     * Pagination metadata inner class
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        /**
         * Current skip value (offset)
         */
        private Integer skip;
        
        /**
         * Current limit value (page size)
         */
        private Integer limit;
        
        /**
         * Total number of records available
         */
        private Long totalRecords;
        
        /**
         * Total number of pages available
         */
        private Integer totalPages;
        
        /**
         * Current page number (0-based)
         */
        private Integer currentPage;
        
        /**
         * Number of records returned in this response
         */
        private Integer recordsReturned;
        
        /**
         * Indicates if there are more records available
         */
        private Boolean hasNext;
        
        /**
         * Indicates if there are previous records available
         */
        private Boolean hasPrevious;
        
        /**
         * Suggested skip value for the next page
         */
        private Integer nextSkip;
        
        /**
         * Suggested skip value for the previous page
         */
        private Integer previousSkip;
        
        /**
         * Calculate pagination metadata
         */
        public static PaginationMetadata calculate(int skip, int limit, long totalRecords, int recordsReturned) {
            int totalPages = (int) Math.ceil((double) totalRecords / limit);
            int currentPage = skip / limit;
            boolean hasNext = (skip + limit) < totalRecords;
            boolean hasPrevious = skip > 0;
            
            return PaginationMetadata.builder()
                    .skip(skip)
                    .limit(limit)
                    .totalRecords(totalRecords)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .recordsReturned(recordsReturned)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .nextSkip(hasNext ? skip + limit : null)
                    .previousSkip(hasPrevious ? Math.max(0, skip - limit) : null)
                    .build();
        }
    }
    
    /**
     * Response status information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseStatus {
        /**
         * HTTP status code
         */
        private Integer code;
        
        /**
         * Status message
         */
        private String message;
        
        /**
         * Response timestamp
         */
        private Long timestamp;
        
        /**
         * Time taken to process the request (in milliseconds)
         */
        private Long processingTimeMs;
        
        /**
         * Warning messages (e.g., approaching timeout, large dataset)
         */
        private List<String> warnings;
        
        /**
         * Create a success status
         */
        public static ResponseStatus success(long processingTimeMs, List<String> warnings) {
            return ResponseStatus.builder()
                    .code(200)
                    .message("Success")
                    .timestamp(System.currentTimeMillis())
                    .processingTimeMs(processingTimeMs)
                    .warnings(warnings)
                    .build();
        }
    }
}
