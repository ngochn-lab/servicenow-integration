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
    private final ServiceNowMapper serviceNowMapper;
    public SuccessFactorsServiceImpl(SAPClient sapClient, ServiceNowMapper serviceNowMapper) {
        this.sapClient = sapClient;
        this.serviceNowMapper = serviceNowMapper;
    }

    @Override
    public EmployeeProfileDTO getEmployeeProfile(String id) {
        // Call success factors API
        BaseResponse sapResponse = sapClient.getEmployeeProfile(id);
        String code = sapResponse.getCode();
        OnboardingCandidateInfoDTO onboardingCandidateInfoDTO = (OnboardingCandidateInfoDTO) sapResponse.getData();
        if ("200".equals(code) && onboardingCandidateInfoDTO != null) {
            return serviceNowMapper.toEmployeeProfileDTO(onboardingCandidateInfoDTO);
        }

        // Map other statuses appropriately
        int statusVal;
        try {
            statusVal = Integer.parseInt(code);
        } catch (Exception e) {
            statusVal = 502; // Bad gateway for unexpected upstream responses
        }
        HttpStatus status = HttpStatus.resolve(statusVal);
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = sapResponse.getMessage() != null ? sapResponse.getMessage() : "Upstream error";
        throw new ResponseStatusException(status, message);
    }
}

