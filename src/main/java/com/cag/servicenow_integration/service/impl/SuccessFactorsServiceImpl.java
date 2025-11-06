package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.response.SapPagedResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.GlobalUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
    public PaginatedResponse<EmployeeProfileDTO> getEmployeeProfiles(int skip, int limit) {
        BaseResponse sapResponse = sapClient.getEmployeeProfiles(skip, limit);
        String code = sapResponse.getCode();
        if ("200".equals(code)) {
            SapPagedResponse<?> sapPaged = (SapPagedResponse<?>) sapResponse.getData();

            List<OnboardingCandidateInfoDTO> content = (List<OnboardingCandidateInfoDTO>) sapPaged.getContent();
            List<EmployeeProfileDTO> mapped = content.stream()
                    .map(serviceNowMapper::toEmployeeProfileDTO)
                    .collect(Collectors.toList());

            long total = sapPaged.getTotalElements();
            boolean hasNext = (skip + mapped.size()) < total;
            Integer nextSkip = hasNext ? skip + mapped.size() : null;

            PaginatedResponse<EmployeeProfileDTO> resp = new PaginatedResponse<>();
            resp.setData(mapped);
            resp.setTotalRecords(total);
            resp.setSkip(skip);
            resp.setLimit(limit);
            resp.setHasNext(hasNext);
            resp.setNextSkip(nextSkip);
            return resp;
        }
        // Map other statuses appropriately
        GlobalUtils.handleOtherStatuses(code, sapResponse.getMessage());
        return null;
    }
}
