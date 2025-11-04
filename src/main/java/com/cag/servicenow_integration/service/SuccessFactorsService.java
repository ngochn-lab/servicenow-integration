package com.cag.servicenow_integration.service;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;

public interface SuccessFactorsService {
    EmployeeProfileDTO getEmployeeProfile(String id);
}
