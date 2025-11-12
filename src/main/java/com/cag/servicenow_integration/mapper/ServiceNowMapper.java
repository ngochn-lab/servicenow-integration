package com.cag.servicenow_integration.mapper;

import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.dto.servicenow.LinkValueDTO;
import com.cag.servicenow_integration.config.ServiceNowApiProperties;
import org.springframework.stereotype.Component;

@Component
public class ServiceNowMapper {
    private final ServiceNowApiProperties serviceNowApiProperties;

    public ServiceNowMapper(ServiceNowApiProperties serviceNowApiProperties) {
        this.serviceNowApiProperties = serviceNowApiProperties;
    }
    // Dung interface Mapstruct
    public EmployeeProfileDTO toEmployeeProfileDTO(OnboardingCandidateInfoDTO onboardingCandidateInfoDTO) {
        EmployeeProfileDTO employeeProfile = new EmployeeProfileDTO();

        if (onboardingCandidateInfoDTO == null) {
            return employeeProfile;
        }

        employeeProfile.setNumber(onboardingCandidateInfoDTO.getApplicantId());
        employeeProfile.setSysId(onboardingCandidateInfoDTO.getCandidateId());
        employeeProfile.setSysCreatedBy(onboardingCandidateInfoDTO.getCreatedBy());
        employeeProfile.setSysCreatedOn(onboardingCandidateInfoDTO.getCreatedDateTime());
        employeeProfile.setEmploymentStartDate(onboardingCandidateInfoDTO.getHireDate());

        // This field might need to connect to offboarding api to get it
        employeeProfile.setEmploymentEndDate(onboardingCandidateInfoDTO.isHired() ? null : onboardingCandidateInfoDTO.getHireDate());
        employeeProfile.setPreferredPronoun(onboardingCandidateInfoDTO.getOnboardingLocale());
        employeeProfile.setShortBio(onboardingCandidateInfoDTO.getExternalName_defaultValue());

        LinkValueDTO user = new LinkValueDTO();
        user.setValue(onboardingCandidateInfoDTO.getUserId());
        String base = serviceNowApiProperties.getInstanceBaseUrl();
        if (base == null) base = "";
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        user.setLink(normalizedBase + "/api/now/table/sys_user/" + onboardingCandidateInfoDTO.getUserId());
        employeeProfile.setUser(user);
        employeeProfile.setWorkMobile("");
        employeeProfile.setWorkPhone("");
        employeeProfile.setPositionType(onboardingCandidateInfoDTO.getJobTitle());
        employeeProfile.setLocationType(onboardingCandidateInfoDTO.getLocation());
        employeeProfile.setSysUpdatedBy(onboardingCandidateInfoDTO.getLastModifiedBy());
        employeeProfile.setSysUpdatedOn(onboardingCandidateInfoDTO.getLastModifiedDateTime());
        employeeProfile.setSysCreatedBy(onboardingCandidateInfoDTO.getCreatedBy());
        employeeProfile.setSysCreatedOn(onboardingCandidateInfoDTO.getCreatedDateTime());
        employeeProfile.setSysModCount(onboardingCandidateInfoDTO.getMdfSystemRecordStatus());

        return employeeProfile;
    }
}
