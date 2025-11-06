package com.cag.servicenow_integration.client;

import com.cag.servicenow_integration.config.SAPApiProperties;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SAPClient {
    private final SAPApiProperties properties;

    private final RestTemplate restTemplate;

    public SAPClient(SAPApiProperties properties) {
        this.properties = properties;
        restTemplate = new RestTemplate();
    }

    public BaseResponse getEmployeeProfile(String id) {
        String url = properties.getBaseUrl() + "/api/service_now/onboarding_candidate_info/" + id;
        BaseResponse baseResponse = new BaseResponse();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String jsonResponse = responseEntity.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode code = root.get("code");
            JsonNode dataNode = root.get("data");

            OnboardingCandidateInfoDTO onboardingCandidateInfo = mapper.treeToValue(dataNode, OnboardingCandidateInfoDTO.class);
            baseResponse.setCode(code.asText());
            baseResponse.setMessage("Success");
            baseResponse.setData(onboardingCandidateInfo);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                baseResponse.setCode("404");
                baseResponse.setMessage("Not Found");
                baseResponse.setData(null);
            } else {
                baseResponse.setCode(String.valueOf(e.getStatusCode().value()));
                baseResponse.setMessage(e.getMessage());
                baseResponse.setData(null);
            }
        } catch (Exception ex) {
            baseResponse.setCode("500");
            baseResponse.setMessage("Internal Server Error");
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    public BaseResponse getEmployeeProfileOld(String id) throws IOException, InterruptedException {
        String url = properties.getBaseUrl() + "/api/service_now/onboarding_candidate_info/" + id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Send request and get response body as a String
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();

        if (jsonResponse.isEmpty() && response.statusCode() == 404) {
            BaseResponse notFoundResponse = new BaseResponse();
            notFoundResponse.setCode("404");
            notFoundResponse.setMessage("Not Found");
            notFoundResponse.setData(null);
            return notFoundResponse;

        }
        // Parse JSON, extract "data" node, and map to DTO
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode code = root.get("code");

        // check code status 200 to proceed or throw exception/return null
        if (!code.asText().equals("200")) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setCode(code.asText());
            errorResponse.setMessage(root.get("message").asText());
            errorResponse.setData(null);
            return errorResponse;
        }
        JsonNode dataNode = root.get("data");
        OnboardingCandidateInfoDTO onboardingCandidateInfo = mapper.treeToValue(dataNode, OnboardingCandidateInfoDTO.class);
        BaseResponse baseResponse = new BaseResponse();

        if (onboardingCandidateInfo != null) {
            baseResponse.setCode("200");
            baseResponse.setMessage("Success");
            baseResponse.setData(onboardingCandidateInfo);
        }
        else {
            baseResponse.setCode("404");
            baseResponse.setMessage("Not Found");
            baseResponse.setData(null);
        }

        return baseResponse;
    }

    private static OnboardingCandidateInfoDTO getOnboardingCandidateInfoDTO() {
        OnboardingCandidateInfoDTO onboardingCandidateInfo = new OnboardingCandidateInfoDTO();
        onboardingCandidateInfo.setApplicantId("APP-20251031-001");
        onboardingCandidateInfo.setCandidateId("CAND-000987654321");
        onboardingCandidateInfo.setCreatedBy("admin@successfactors.com");
        onboardingCandidateInfo.setCreatedDateTime("/Date(1730408464000)/");
        onboardingCandidateInfo.setCrossboarded(false);
        onboardingCandidateInfo.setDepartment("Engineering");
        onboardingCandidateInfo.setDivision("Software Development");
        onboardingCandidateInfo.setEmail("alex.nguyen@example.com");
        onboardingCandidateInfo.setExternalName_de_DE("Alex Nguyen (DE)");
        onboardingCandidateInfo.setExternalName_defaultValue("Alex Nguyen");
        onboardingCandidateInfo.setExternalName_en_GB("Alex Nguyen (UK)");
        onboardingCandidateInfo.setExternalName_en_US("Alex Nguyen");
        onboardingCandidateInfo.setExternalName_es_ES("Alex Nguyen (ES)");
        onboardingCandidateInfo.setExternalName_fr_FR("Alex Nguyen (FR)");
        onboardingCandidateInfo.setExternalName_ja_JP("アレックス・グエン");
        onboardingCandidateInfo.setExternalName_ko_KR("알렉스 응우옌");
        onboardingCandidateInfo.setExternalName_localized("Alex Nguyen");
        onboardingCandidateInfo.setExternalName_nl_NL("Alex Nguyen (NL)");
        onboardingCandidateInfo.setExternalName_pt_BR("Alex Nguyen (BR)");
        onboardingCandidateInfo.setExternalName_pt_PT("Alex Nguyen (PT)");
        onboardingCandidateInfo.setExternalName_ru_RU("Алекс Нгуен");
        onboardingCandidateInfo.setExternalName_zh_CN("阮亚历克斯");
        onboardingCandidateInfo.setExternalName_zh_TW("阮亞歷克斯");
        onboardingCandidateInfo.setFName("Alex");
        onboardingCandidateInfo.setFailedSEBEventsOccured(false);
        onboardingCandidateInfo.setFromExternalATS(true);
        onboardingCandidateInfo.setGlobalAssignment(false);
        onboardingCandidateInfo.setHireDate("2025-11-15");
        onboardingCandidateInfo.setHired(true);
        onboardingCandidateInfo.setHrManagerId("HRM12345");
        onboardingCandidateInfo.setInternalHire(false);
        onboardingCandidateInfo.setJobReqId("REQ-2025-ENG-7890");
        onboardingCandidateInfo.setJobTitle("Java Backend Developer");
        onboardingCandidateInfo.setKmsUserId("ALEX_NGUYEN_001");
        onboardingCandidateInfo.setLName("Nguyen");
        onboardingCandidateInfo.setLastModifiedBy("susan.lead@example.com");
        onboardingCandidateInfo.setLastModifiedDateTime("/Date(1730412064000)/");
        onboardingCandidateInfo.setLocation("Ho Chi Minh City");
        onboardingCandidateInfo.setManagerId("MGR56789");
        onboardingCandidateInfo.setMdfSystemRecordStatus("ACTIVE");
        onboardingCandidateInfo.setOnboardingLocale("en_US");
        onboardingCandidateInfo.setPayGrade("PG-6");
        onboardingCandidateInfo.setProcessorId("PROC001");
        onboardingCandidateInfo.setReadyToHire(true);
        onboardingCandidateInfo.setUserId("U123456");
        onboardingCandidateInfo.setWorkCountry("VN");
        return onboardingCandidateInfo;
    }
}
