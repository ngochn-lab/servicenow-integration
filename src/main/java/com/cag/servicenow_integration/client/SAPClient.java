package com.cag.servicenow_integration.client;

import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class SAPClient {
    @Value("${sap.api.url}")
    private String sapApiUrl;

    @Value("${sap.api.auth.token}")
    private String authToken;

    @Value("${sap.api.onboarding.candidate.endpoint}")
    private String onboardingCandidateEndpoint;

    private final RestTemplate restTemplate;

    public SAPClient() {
        restTemplate = new RestTemplate();
    }

    public BaseResponse getEmployeeProfile(String id) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(authToken);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<BaseResponse> response = restTemplate.getForEntity(sapApiUrl + onboardingCandidateEndpoint + id, BaseResponse.class, entity);
//        return response;
        System.out.println("Hard-coded data from SAP");

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("200");
        baseResponse.setMessage("Success");

        // Mock data for testing
        OnboardingCandidateInfoDTO onboardingCandidateInfo = getOnboardingCandidateInfoDTO();

        baseResponse.setData(onboardingCandidateInfo);

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
