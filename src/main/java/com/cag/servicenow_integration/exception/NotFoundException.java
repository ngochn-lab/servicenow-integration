package com.cag.servicenow_integration.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends DomainException {
    public NotFoundException(String message, int responseCode, String errorCode) {
        super(message, responseCode, errorCode);
    }

    public NotFoundException(String message, int responseCode, String errorCode, Object... arguments) {
        super(message, responseCode, errorCode, arguments);
    }
}
