package com.cag.servicenow_integration.client;

import com.cag.servicenow_integration.config.SAPApiProperties;
import com.cag.servicenow_integration.constants.Constants;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.enums.ResponseCode;
import com.cag.servicenow_integration.exception.DomainException;
import com.cag.servicenow_integration.exception.InternalServerException;
import com.cag.servicenow_integration.exception.NotFoundException;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.response.sap.PageMetaDTO;
import com.cag.servicenow_integration.response.sap.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SAPClient {
    private final SAPApiProperties sapApiProperties;

    private final WebClient webClient;

    public SAPClient(SAPApiProperties sapApiProperties, WebClient webClient) {
        this.sapApiProperties = sapApiProperties;
        this.webClient = webClient;
    }

    public BaseResponse getEmployeeProfiles(int skip, int limit) {
        String url = sapApiProperties.getBaseUrl() + sapApiProperties.getOnboardingCandidateEndpoint() + "?skip=" + skip + "&limit=" + limit;
        try {
            log.debug("Requesting SAP profiles with url: " + url);
            // Expect Domain response
            Mono<ResponseDTO<PageMetaDTO<OnboardingCandidateInfoDTO>>> response = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (StringUtils.hasText(sapApiProperties.getAuthToken())) {
                            headers.setBearerAuth(sapApiProperties.getAuthToken());
                        }
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseDTO<PageMetaDTO<OnboardingCandidateInfoDTO>>>() {
                    });

            ResponseDTO<PageMetaDTO<OnboardingCandidateInfoDTO>> sapResponse = response.block();

            if (sapResponse == null) throw new InternalServerException(
                    Constants.SAP_RESPONSE_NULL_MSG,
                    ResponseCode.ERROR.getCode(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            );

            if (sapResponse.getCode() != HttpStatus.OK.value()) {
                return BaseResponse.builder()
                        .responseCode(sapResponse.getStatus())
                        .code(String.valueOf(sapResponse.getCode()))
                        .message(sapResponse.getMessage())
                        .build();
            }

            if (sapResponse.getData().getContent().isEmpty()) {
                PaginatedResponse<OnboardingCandidateInfoDTO> paginatedResponse = PaginatedResponse.<OnboardingCandidateInfoDTO>builder()
                        .totalRecords(sapResponse.getData().getTotalElements())
                        .skip(sapResponse.getData().getNumber())
                        .limit(sapResponse.getData().getSize())
                        .hasNext(!sapResponse.getData().isLast())
                        .nextSkip(sapResponse.getData().getNumber() + sapResponse.getData().getSize())
                        .build();
//                throw new NotFoundException(Constants.NOT_FOUND_MSG + paginatedResponse, ResponseCode.NOT_FOUND.getCode(), String.valueOf(HttpStatus.NOT_FOUND.value()));
            }

            PaginatedResponse<OnboardingCandidateInfoDTO> paginatedResponse = PaginatedResponse.<OnboardingCandidateInfoDTO>builder()
                    .data(sapResponse.getData().getContent())
                    .totalRecords(sapResponse.getData().getTotalElements())
                    .skip(sapResponse.getData().getNumber())
                    .limit(sapResponse.getData().getSize())
                    .hasNext(!sapResponse.getData().isLast())
                    .nextSkip(sapResponse.getData().getNumber() + sapResponse.getData().getSize())
                    .build();

            return BaseResponse.builder()
                    .responseCode(ResponseCode.SUCCESS.getCode())
                    .code(String.valueOf(HttpStatus.OK.value()))
                    .message(Constants.SUCCESS_MSG)
                    .data(paginatedResponse)
                    .build();
        } catch (WebClientResponseException.NotFound ex) {
            log.warn("Onboarding Candidate Info list is empty: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage(), ResponseCode.NOT_FOUND.getCode(), String.valueOf(HttpStatus.NOT_FOUND.value()));
        } catch (WebClientResponseException ex) {
            log.error("Unexpected error calling SAP: {}", ex.getMessage(), ex);
            throw new InternalServerException(ex.getMessage(), ResponseCode.ERROR.getCode(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (DomainException ex) {
            log.error("Middleware error: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public BaseResponse getEmployeeProfile(String id) {
        String url = buildUrl(sapApiProperties.getBaseUrl(), sapApiProperties.getOnboardingCandidateEndpoint(), id);

        try {
            log.debug("Requesting a SAP profile url: " + url);
            Mono<ResponseDTO<OnboardingCandidateInfoDTO>> response = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (StringUtils.hasText(sapApiProperties.getAuthToken())) {
                            headers.setBearerAuth(sapApiProperties.getAuthToken());
                        }
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseDTO<OnboardingCandidateInfoDTO>>() {
                    });

            ResponseDTO<OnboardingCandidateInfoDTO> sapResponse = response.block();
            if (sapResponse == null) {
                throw new InternalServerException(
                        Constants.SAP_RESPONSE_NULL_MSG,
                        ResponseCode.ERROR.getCode(),
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
                );
            }

            if (sapResponse.getCode() != HttpStatus.OK.value()) {
                return BaseResponse.builder()
                        .responseCode(sapResponse.getStatus())
                        .code(String.valueOf(sapResponse.getCode()))
                        .message(sapResponse.getMessage())
                        .build();
            }

            return BaseResponse.builder()
                    .responseCode(0)
                    .code(String.valueOf(HttpStatus.OK.value()))
                    .message(Constants.SUCCESS_MSG)
                    .data(sapResponse.getData())
                    .build();
        } catch (WebClientResponseException.NotFound ex) {
            log.warn("Onboarding Candidate Info not found for id {}: {}", id, ex.getMessage());
            throw new NotFoundException(ex.getMessage(), ResponseCode.NOT_FOUND.getCode(), String.valueOf(HttpStatus.NOT_FOUND.value()));
        } catch (WebClientResponseException ex) {
            log.error("Unexpected error calling SAP: {}", ex.getMessage(), ex);
            throw new InternalServerException(ex.getMessage(), ResponseCode.ERROR.getCode(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (DomainException ex) {
            log.error(Constants.MIDDLEWARE_ERROR_MSG, ex.getMessage(), ex);
            throw ex;
        }
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
