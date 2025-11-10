package com.cag.servicenow_integration.response;

import lombok.*;

@Data
@Builder
public class BaseResponse {
    private Integer responseCode;
    private String code;
    private String message;
    private Object data;
}
