package com.cag.servicenow_integration.constants;

import org.springframework.http.MediaType;

public class Constants {
    private Constants (){
        // private constructor to prevent instantiation
    }

    public static final MediaType MEDIA_TYPE_CSV = MediaType.parseMediaType("text/csv");
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public static final String CONTENT_DISPOSITION_FILENAME_TEMPLATE = "attachment; filename=\"employee_profile_%s.csv\"";

    public static final String MIDDLEWARE_ERROR_MSG = "Middleware Error: {}";
    public static final String SAP_RESPONSE_NULL_MSG = "SAP response is null";

    public static final String SUCCESS_MSG = "Success";
    public static final String NOT_FOUND_MSG = "Not Found";
}
