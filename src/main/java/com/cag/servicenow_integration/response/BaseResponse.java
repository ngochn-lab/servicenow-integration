package com.cag.servicenow_integration.response;

import lombok.*;

@Data
@Builder
public class BaseResponse {
    private int responseCode;
    private String code;
    private String message;
    private Object data;
}
