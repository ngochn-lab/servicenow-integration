package com.cag.servicenow_integration.enums;

public enum ResponseCode {
    SUCCESS(0),
    NOT_FOUND(1),
    ERROR(2);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
