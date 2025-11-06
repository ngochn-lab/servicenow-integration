package com.cag.servicenow_integration.response;

import lombok.Data;

import java.util.List;

@Data
public class SapPagedResponse<T> {
    private List<T> content;
    private long totalElements;
}
