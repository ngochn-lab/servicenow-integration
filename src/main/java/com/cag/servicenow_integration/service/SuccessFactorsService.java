package com.cag.servicenow_integration.service;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.request.ServiceNowRequest;
import com.cag.servicenow_integration.response.ServiceNowResponse;

public interface SuccessFactorsService {
    EmployeeProfileDTO getEmployeeProfile(String id);
}
