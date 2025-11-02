package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuccessFactorsServiceImpl implements SuccessFactorsService {
    @Autowired
    private SAPClient sapClient;

    @Override
    public EmployeeProfileDTO getEmployeeProfile(String id) {
        // Call success factors API
        BaseResponse sapResponse = sapClient.getEmployeeProfile(id);
        OnboardingCandidateInfoDTO onboardingCandidateInfoDTO = (OnboardingCandidateInfoDTO) sapResponse.getData();
        EmployeeProfileDTO employeeProfile = new EmployeeProfileDTO();

        if (sapResponse.getCode().equals("200")) {
            // Return transformed response
            employeeProfile = ServiceNowMapper.toEmployeeProfileDTO(onboardingCandidateInfoDTO);
        }

        return employeeProfile;
    }

    private boolean isHired(String date) {
        return date != null && !date.isEmpty();
    }
}
