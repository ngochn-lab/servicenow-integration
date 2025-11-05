package com.cag.servicenow_integration.service;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuccessFactorsService {
    EmployeeProfileDTO getEmployeeProfile(String id);
    Page<EmployeeProfileDTO> getEmployeeProfiles(Pageable pageable);
}
