package com.cag.servicenow_integration.response.sap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO<T> {
    private int status;
    private int code;
    private String message;
    private T data;
}
