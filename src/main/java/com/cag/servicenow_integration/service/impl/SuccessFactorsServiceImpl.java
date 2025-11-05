package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.GlobalUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

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
        GlobalUtils.handleOtherStatuses(code, sapResponse.getMessage());
        return null;
    }

    @Override
    public Page<EmployeeProfileDTO> getEmployeeProfiles(Pageable pageable) {
        BaseResponse sapResponse = sapClient.getEmployeeProfiles(pageable);
        String code = sapResponse.getCode();
        if ("200".equals(code)) {
            List<OnboardingCandidateInfoDTO> onboardingCandidateInfoDTOList = (List<OnboardingCandidateInfoDTO>) sapResponse.getData();
            return new PageImpl<>(onboardingCandidateInfoDTOList, pageable, onboardingCandidateInfoDTOList.size()).map(serviceNowMapper::toEmployeeProfileDTO);
        }
        // Map other statuses appropriately
        GlobalUtils.handleOtherStatuses(code, sapResponse.getMessage());
        return null;
    }
}

