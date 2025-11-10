package com.cag.servicenow_integration.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {
    private List<T> data;
    private long totalRecords;
    private int skip;
    private int limit;
    private boolean hasNext;
    private Integer nextSkip;
}
