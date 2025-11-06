package com.cag.servicenow_integration.service;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.PaginatedResponse;

public interface SuccessFactorsService {
    EmployeeProfileDTO getEmployeeProfile(String id);
    PaginatedResponse<EmployeeProfileDTO> getEmployeeProfiles(int skip, int limit);
}
