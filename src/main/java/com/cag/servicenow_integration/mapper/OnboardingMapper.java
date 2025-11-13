package com.cag.servicenow_integration.mapper;

import com.cag.servicenow_integration.domain.OnboardingCandidateInfo;
import com.cag.servicenow_integration.dto.sap.OnboardingCandidateInfoDTO;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.dto.servicenow.LinkValueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {
    @Mapping(target = "fullName", expression = "java(dto.getFName() + \" \" + dto.getLName())")
    OnboardingCandidateInfo toDomain(OnboardingCandidateInfoDTO dto);

    @Mapping(target = "number", source = "applicantId")
    @Mapping(target = "sysId", source = "candidateId")
    @Mapping(target = "legalName", source = "fullName")
    @Mapping(target = "positionType", source = "jobTitle")
    @Mapping(target = "employmentStartDate", source = "hireDate")
    @Mapping(target = "locationType", source = "location")
    @Mapping(target = "sysCreatedOn", source = "createdDateTime")
    @Mapping(target = "sysCreatedBy", source = "createdBy")
    @Mapping(target = "sysUpdatedOn", source = "lastModifiedDateTime")
    @Mapping(target = "sysUpdatedBy", source = "lastModifiedBy")
    @Mapping(target = "user", expression = "java(mapCandidateIdToUser(domain.getCandidateId()))")
    EmployeeProfileDTO toEmployeeProfileDTO(OnboardingCandidateInfo domain);

    default LinkValueDTO mapCandidateIdToUser(String candidateId) {
        LinkValueDTO user = new LinkValueDTO();
        user.setLink("https://dev313338.service-now.com/api/now/table/sys_user/" + candidateId);
        user.setValue(candidateId);
        return user;
    }
}