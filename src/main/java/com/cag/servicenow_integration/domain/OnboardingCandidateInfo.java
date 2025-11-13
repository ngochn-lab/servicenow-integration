package com.cag.servicenow_integration.domain;

import lombok.Data;

@Data
public class OnboardingCandidateInfo {
    private String applicantId;
    private String candidateId;
    private String fullName;
    private String jobTitle;
    private String hireDate;
    private String location;
    private String createdBy;
    private String createdDateTime;
    private String lastModifiedBy;
    private String lastModifiedDateTime;
}
