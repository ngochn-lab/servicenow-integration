package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.config.TimeoutConfiguration;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.ServiceNowMapper;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.GlobalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SuccessFactorsServiceImpl implements SuccessFactorsService {
    private final SAPClient sapClient;
    private final ServiceNowMapper serviceNowMapper;
    private final TimeoutConfiguration timeoutConfiguration;
    private final ExecutorService executorService;
    
    public SuccessFactorsServiceImpl(SAPClient sapClient, 
                                    ServiceNowMapper serviceNowMapper,
                                    TimeoutConfiguration timeoutConfiguration) {
        this.sapClient = sapClient;
        this.serviceNowMapper = serviceNowMapper;
        this.timeoutConfiguration = timeoutConfiguration;
        // Create a thread pool for handling timeouts
        this.executorService = Executors.newCachedThreadPool();
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
    @Deprecated
    public List<EmployeeProfileDTO> getEmployeeProfiles(Integer page, Integer size) {
        // Legacy implementation - convert to skip/limit
        int skip = page * size;
        PaginatedResponse<EmployeeProfileDTO> response = getEmployeeProfilesPaginated(skip, size);
        return response.getData();
    }

    @Override
    public PaginatedResponse<EmployeeProfileDTO> getEmployeeProfilesPaginated(Integer skip, Integer limit) {
        log.info("Fetching employee profiles with skip={}, limit={}", skip, limit);

        // Set timeout for paginated requests
        int timeoutSeconds = timeoutConfiguration.getTimeoutForRequest(true);

        Future<PaginatedResponse<EmployeeProfileDTO>> future = executorService.submit(() -> {
            try {
                // Convert skip/limit to page/size for SAP client (if needed)
                int page = skip / limit;
                int size = limit;

                // Fetch data from SAP
                BaseResponse sapResponse = sapClient.getEmployeeProfiles(page, size);
                String code = sapResponse.getCode();

                if ("200".equals(code)) {
                    List<OnboardingCandidateInfoDTO> onboardingCandidateInfoDTOList =
                        (List<OnboardingCandidateInfoDTO>) sapResponse.getData();

                    // Map to DTOs
                    List<EmployeeProfileDTO> employeeProfiles = onboardingCandidateInfoDTOList.stream()
                        .map(serviceNowMapper::toEmployeeProfileDTO)
                        .collect(Collectors.toList());

                    // Get total count (this might need a separate API call)
                    Long totalCount = getTotalEmployeeProfilesCount();

                    // Build paginated response
                    return PaginatedResponse.<EmployeeProfileDTO>builder()
                        .data(employeeProfiles)
                        .pagination(PaginatedResponse.PaginationMetadata.calculate(
                            skip, limit, totalCount, employeeProfiles.size()))
                        .build();
                }

                // Handle error cases
                GlobalUtils.handleOtherStatuses(code, sapResponse.getMessage());
                return createEmptyPaginatedResponse(skip, limit);

            } catch (Exception e) {
                log.error("Error fetching employee profiles", e);
                throw new RuntimeException("Failed to fetch employee profiles", e);
            }
        });

        try {
            // Wait for result with timeout
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Timeout occurred while fetching employee profiles (timeout: {} seconds)", timeoutSeconds);
            future.cancel(true);
            throw new RuntimeException("Request timed out after " + timeoutSeconds + " seconds", e);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error executing paginated request", e);
            throw new RuntimeException("Failed to execute paginated request", e);
        }
    }

    @Override
    public Long getTotalEmployeeProfilesCount() {
        // This would typically call a count endpoint from SAP
        // For now, returning a placeholder value
        // TODO: Implement actual count API call
        log.warn("Using placeholder total count - implement actual count API");
        return 1000L; // Placeholder
    }

    private PaginatedResponse<EmployeeProfileDTO> createEmptyPaginatedResponse(Integer skip, Integer limit) {
        return PaginatedResponse.<EmployeeProfileDTO>builder()
            .data(new ArrayList<>())
            .pagination(PaginatedResponse.PaginationMetadata.calculate(skip, limit, 0, 0))
            .build();
    }
}

