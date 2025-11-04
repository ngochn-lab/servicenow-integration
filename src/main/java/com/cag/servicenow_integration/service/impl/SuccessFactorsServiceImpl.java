package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SuccessFactorsServiceImpl implements SuccessFactorsService {
    private final SAPClient sapClient;

    public SuccessFactorsServiceImpl(SAPClient sapClient) {
        this.sapClient = sapClient;
    }

    @Override
    public EmployeeProfileDTO getEmployeeProfile(String id) {
        // Call success factors API
        BaseResponse sapResponse = sapClient.getEmployeeProfile(id);
        OnboardingCandidateInfoDTO onboardingCandidateInfoDTO = (OnboardingCandidateInfoDTO) sapResponse.getData();
        if (onboardingCandidateInfoDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found");
        }
        EmployeeProfileDTO employeeProfile = new EmployeeProfileDTO();
        employeeProfile = ServiceNowMapper.toEmployeeProfileDTO(onboardingCandidateInfoDTO); // Return transformed response
        return employeeProfile;
    }
}
