package com.cag.servicenow_integration.dto.sap;

import lombok.Data;

@Data
public class OnboardingCandidateInfoDTO {
    private String applicantId;
    private String candidateId;
    private String createdBy;
    private String createdDateTime;
    private boolean crossboarded;
    private String department;
    private String division;
    private String email;
    private String externalName_de_DE;
    private String externalName_defaultValue;
    private String externalName_en_GB;
    private String externalName_en_US;
    private String externalName_es_ES;
    private String externalName_fr_FR;
    private String externalName_ja_JP;
    private String externalName_ko_KR;
    private String externalName_localized;
    private String externalName_nl_NL;
    private String externalName_pt_BR;
    private String externalName_pt_PT;
    private String externalName_ru_RU;
    private String externalName_zh_CN;
    private String externalName_zh_TW;
    private String fName;
    private boolean failedSEBEventsOccured;
    private boolean fromExternalATS;
    private boolean globalAssignment;
    private String hireDate;
    private boolean hired;
    private String hrManagerId;
    private boolean internalHire;
    private String jobReqId;
    private String jobTitle;
    private String kmsUserId;
    private String lName;
    private String lastModifiedBy;
    private String lastModifiedDateTime;
    private String location;
    private String managerId;
    private String mdfSystemRecordStatus;
    private String onboardingLocale;
    private String payGrade;
    private String processorId;
    private boolean readyToHire;
    private String userId;
    private String workCountry;
}
