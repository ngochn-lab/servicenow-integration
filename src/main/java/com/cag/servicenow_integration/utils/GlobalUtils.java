package com.cag.servicenow_integration.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GlobalUtils {
    public static void handleOtherStatuses(String statusCode, String message){
        int statusVal;
        try {
            statusVal = Integer.parseInt(statusCode);
        } catch (Exception e) {
            statusVal = 502; // Bad gateway for unexpected upstream responses
        }
        HttpStatus status = HttpStatus.resolve(statusVal);
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = message != null ? message : "Upstream error";
        throw new ResponseStatusException(status, msg);
    }
}
