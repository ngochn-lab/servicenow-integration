package com.cag.servicenow_integration.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final int responseCode;
    private final String errorCode;
    private final Object[] arguments;

    public DomainException(String message, final int responseCode, String errorCode) {
        super(message);
        this.responseCode = responseCode;
        this.errorCode = errorCode;
        this.arguments = new Object[0];
    }

    public DomainException(String message, final int responseCode, String errorCode, Object... arguments) {
        super(message);
        this.responseCode = responseCode;
        this.errorCode = errorCode;
        this.arguments = arguments;
    }
}
