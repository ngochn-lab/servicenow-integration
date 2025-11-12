package com.cag.servicenow_integration.response.sap;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageMetaDTO<T> {
    private List<T> content;
    private int totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;
    private boolean last;
    private boolean first;
    private boolean empty;
}
