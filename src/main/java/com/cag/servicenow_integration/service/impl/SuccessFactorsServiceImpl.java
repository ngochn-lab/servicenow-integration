package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        if (String.valueOf(HttpStatus.OK.value()).equals(code) && onboardingCandidateInfoDTO != null) {
            return serviceNowMapper.toEmployeeProfileDTO(onboardingCandidateInfoDTO);
        }
        return null;
    }

    @Override
    public PaginatedResponse<EmployeeProfileDTO> getEmployeeProfiles(int skip, int limit) {
        BaseResponse sapResponse = sapClient.getEmployeeProfiles(skip, limit);
        if (!String.valueOf(HttpStatus.OK.value()).equals(sapResponse.getCode())) return null;

        PaginatedResponse<OnboardingCandidateInfoDTO> sapPaginatedResponse = (PaginatedResponse<OnboardingCandidateInfoDTO>) sapResponse.getData();

        List<OnboardingCandidateInfoDTO> content = Optional.ofNullable(sapPaginatedResponse.getData()).orElse(Collections.emptyList());
        List<EmployeeProfileDTO> serviceNowResponse = content.stream()
                .map(serviceNowMapper::toEmployeeProfileDTO)
                .toList();

        return PaginatedResponse.<EmployeeProfileDTO>builder()
                .data(serviceNowResponse)
                .totalRecords(sapPaginatedResponse.getTotalRecords())
                .skip(sapPaginatedResponse.getSkip())
                .limit(sapPaginatedResponse.getLimit())
                .hasNext(sapPaginatedResponse.isHasNext())
                .nextSkip(sapPaginatedResponse.getNextSkip())
                .build();
    }
}
