package com.cag.servicenow_integration.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends DomainException {
    public InternalServerException(String message, int responseCode, String errorCode) {
        super(message, responseCode, errorCode);
    }

    public InternalServerException(String message, int responseCode, String errorCode, Object... arguments) {
        super(message, responseCode, errorCode, arguments);
    }
}
