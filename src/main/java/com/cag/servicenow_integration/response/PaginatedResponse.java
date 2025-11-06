package com.cag.servicenow_integration.response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse<T> {
    private List<T> data;
    private long totalRecords;
    private int skip;
    private int limit;
    private boolean hasNext;
    private Integer nextSkip;
}
