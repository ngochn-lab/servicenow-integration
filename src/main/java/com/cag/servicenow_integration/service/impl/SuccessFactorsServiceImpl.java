package com.cag.servicenow_integration.service.impl;

import com.cag.servicenow_integration.client.SAPClient;
import com.cag.servicenow_integration.config.SAPApiProperties;
import com.cag.servicenow_integration.domain.OnboardingCandidateInfo;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.mapper.OnboardingMapper;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.response.sap.PageMetaDTO;
import com.cag.servicenow_integration.response.sap.ResponseDTO;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SuccessFactorsServiceImpl implements SuccessFactorsService {
    private final SAPClient sapClient;

    private final SAPApiProperties sapApiProperties;

    private final OnboardingMapper onboardingMapper;

    public SuccessFactorsServiceImpl(SAPClient sapClient, SAPApiProperties sapApiProperties, OnboardingMapper onboardingMapper) {
        this.sapClient = sapClient;
        this.sapApiProperties = sapApiProperties;
        this.onboardingMapper = onboardingMapper;
    }

    @Override
    public EmployeeProfileDTO getEmployeeProfile(String id) {
        String url = buildUrl(sapApiProperties.getBaseUrl(), sapApiProperties.getOnboardingCandidateEndpoint(), id);
        log.debug("Requesting SAP profile with url: ", url);
        ResponseDTO<OnboardingCandidateInfoDTO> sapResponse = sapClient.getRequest(url, new ParameterizedTypeReference<ResponseDTO<OnboardingCandidateInfoDTO>>() {});
        OnboardingCandidateInfoDTO onboardingCandidateInfoDTO = sapResponse.getData();
        if (HttpStatus.OK.value() == sapResponse.getCode() && onboardingCandidateInfoDTO != null) {
            OnboardingCandidateInfo onboardingCandidateInfo = onboardingMapper.toDomain(onboardingCandidateInfoDTO);
            return onboardingMapper.toEmployeeProfileDTO(onboardingCandidateInfo);
        }
        return null;
    }

    @Override
    public PaginatedResponse<EmployeeProfileDTO> getEmployeeProfiles(int skip, int limit) {
        String url = sapApiProperties.getBaseUrl() + sapApiProperties.getOnboardingCandidateEndpoint() + "?skip=" + skip + "&limit=" + limit;
        log.debug("Requesting SAP profiles with url: " + url);
        ResponseDTO<PageMetaDTO<OnboardingCandidateInfoDTO>> sapResponse = sapClient.getRequest(url, new ParameterizedTypeReference<ResponseDTO<PageMetaDTO<OnboardingCandidateInfoDTO>>>() {});
        if (HttpStatus.OK.value() != sapResponse.getCode()) return null;
        PaginatedResponse<OnboardingCandidateInfoDTO> paginatedResponse = PaginatedResponse.<OnboardingCandidateInfoDTO>builder()
                .data(sapResponse.getData().getContent())
                .totalRecords(sapResponse.getData().getTotalElements())
                .skip(sapResponse.getData().getNumber())
                .limit(sapResponse.getData().getSize())
                .hasNext(!sapResponse.getData().isLast())
                .nextSkip(sapResponse.getData().getNumber() + sapResponse.getData().getSize())
                .build();

        List<OnboardingCandidateInfo> content = Optional.ofNullable(paginatedResponse.getData())
                .orElse(Collections.emptyList()).stream()
                .map(onboardingMapper::toDomain)
                .toList();
        List<EmployeeProfileDTO> serviceNowResponse = content.stream()
                .map(onboardingMapper::toEmployeeProfileDTO)
                .toList();

        return PaginatedResponse.<EmployeeProfileDTO>builder()
                .data(serviceNowResponse)
                .totalRecords(paginatedResponse.getTotalRecords())
                .skip(paginatedResponse.getSkip())
                .limit(paginatedResponse.getLimit())
                .hasNext(paginatedResponse.isHasNext())
                .nextSkip(paginatedResponse.getNextSkip())
                .build();
    }

    private String buildUrl(String baseUrl, String endpoint, String id) {
        if (baseUrl == null) baseUrl = "";
        if (endpoint == null) endpoint = "";
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        if (!normalizedEndpoint.endsWith("/")) {
            normalizedEndpoint = normalizedEndpoint + "/";
        }
        return normalizedBase + normalizedEndpoint + id;
    }
}
