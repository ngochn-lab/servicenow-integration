package com.cag.servicenow_integration.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(OffsetDateTime timestamp, int responseCode, String errorCode, List<String> messages, String path) {}