package com.cag.servicenow_integration.client;

import com.cag.servicenow_integration.config.SAPApiProperties;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.response.BaseResponse;
import com.cag.servicenow_integration.response.SapPagedResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class SAPClient {
    private final SAPApiProperties properties;

    private final RestTemplate restTemplate;

    public SAPClient(SAPApiProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public BaseResponse getEmployeeProfiles(int skip, int limit) {
        String url = properties.getBaseUrl() + properties.getOnboardingCandidateEndpoint() + "?skip=" + skip + "&limit=" + limit;
        BaseResponse baseResponse = new BaseResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (StringUtils.hasText(properties.getAuthToken())) {
                headers.setBearerAuth(properties.getAuthToken());
            }
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            log.debug("Requesting SAP profiles with url: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            String jsonResponse = responseEntity.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode code = root.get("code");
            JsonNode dataNode = root.get("data");

            String codeText = code != null ? code.asText() : String.valueOf(responseEntity.getStatusCode().value());
            if (!"200".equals(codeText)) {
                baseResponse.setCode(codeText);
                baseResponse.setMessage(root.has("message") ? root.get("message").asText() : responseEntity.getBody());
                baseResponse.setData(null);
                return baseResponse;
            }
            JsonNode contentNode = dataNode.get("content");
            long totalElements = dataNode.has("totalElements") ? dataNode.get("totalElements").asLong() : (contentNode != null ? contentNode.size() : 0);
            List<OnboardingCandidateInfoDTO> onboardingCandidateInfos = mapper.readerForListOf(OnboardingCandidateInfoDTO.class).readValue(contentNode);
            SapPagedResponse<OnboardingCandidateInfoDTO> sapPaged = new SapPagedResponse<>();
            sapPaged.setContent(onboardingCandidateInfos);
            sapPaged.setTotalElements(totalElements);
            baseResponse.setCode("200");
            baseResponse.setMessage("Success");
            baseResponse.setData(sapPaged);
        } catch (Exception ex) {
            log.error("Unexpected error calling SAP: {}", ex.getMessage(), ex);
            baseResponse.setCode("500");
            baseResponse.setMessage("Internal Server Error");
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    public BaseResponse getEmployeeProfile(String id) {
        String url = buildUrl(properties.getBaseUrl(), properties.getOnboardingCandidateEndpoint(), id);
        BaseResponse baseResponse = new BaseResponse();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (StringUtils.hasText(properties.getAuthToken())) {
                headers.setBearerAuth(properties.getAuthToken());
            }
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            log.debug("Requesting a SAP profile url: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            String jsonResponse = responseEntity.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode code = root.get("code");
            JsonNode dataNode = root.get("data");

            String codeText = code != null ? code.asText() : String.valueOf(responseEntity.getStatusCode().value());
            if (!"200".equals(codeText)) {
                baseResponse.setCode(codeText);
                baseResponse.setMessage(root.has("message") ? root.get("message").asText() : responseEntity.getBody());
                baseResponse.setData(null);
                return baseResponse;
            }

            OnboardingCandidateInfoDTO onboardingCandidateInfo = mapper.treeToValue(dataNode, OnboardingCandidateInfoDTO.class);
            baseResponse.setCode("200");
            baseResponse.setMessage("Success");
            baseResponse.setData(onboardingCandidateInfo);
        } catch (RestClientResponseException e) {
            log.warn("SAP profile request failed. status={} message={}", e.getStatusCode(), e.getStatusText());
            baseResponse.setCode(String.valueOf(e.getStatusCode()));
            baseResponse.setMessage(e.getStatusText());
            baseResponse.setData(null);
        } catch (Exception ex) {
            log.error("Unexpected error calling SAP: {}", ex.getMessage(), ex);
            baseResponse.setCode("500");
            baseResponse.setMessage("Internal Server Error");
            baseResponse.setData(null);
        }
        return baseResponse;
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
