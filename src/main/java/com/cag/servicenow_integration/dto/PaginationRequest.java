package com.cag.servicenow_integration.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination request parameters using skip/limit pattern.
 * This approach provides better control over data retrieval and prevents data overload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    
    /**
     * Number of records to skip from the beginning (offset).
     * Default: 0 (start from the first record)
     */
    @Min(value = 0, message = "Skip value must be non-negative")
    @Builder.Default
    private Integer skip = 0;
    
    /**
     * Maximum number of records to return (page size).
     * Default: 50, Maximum: 200 to prevent data overload
     */
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 200, message = "Limit cannot exceed 200 to prevent data overload")
    @Builder.Default
    private Integer limit = 50;
    
    /**
     * Calculate the page number based on skip and limit
     */
    public int getPageNumber() {
        if (limit == null || limit <= 0) {
            return 0;
        }
        return skip / limit;
    }
    
    /**
     * Validate and sanitize pagination parameters
     */
    public void sanitize() {
        if (skip == null || skip < 0) {
            skip = 0;
        }
        if (limit == null || limit < 1) {
            limit = 50;
        } else if (limit > 200) {
            limit = 200;
        }
    }
}
