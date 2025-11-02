package com.cag.servicenow_integration.response;

import lombok.*;

@Getter
@Setter
public class BaseResponse {
    private String code;
    private String message;
    private Object data;
}
