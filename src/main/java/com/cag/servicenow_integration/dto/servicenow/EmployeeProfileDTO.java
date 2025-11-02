package com.cag.servicenow_integration.dto.servicenow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeProfileDTO {
    @JsonProperty("background_banner")
    private String backgroundBanner;
    @JsonProperty("employment_start_date")
    private String employmentStartDate;
    @JsonProperty("short_bio")
    private String shortBio;
    @JsonProperty("sys_mod_count")
    private String sysModCount;
    @JsonProperty("work_mobile")
    private String workMobile;
    @JsonProperty("work_phone")
    private String workPhone;
    @JsonProperty("sys_updated_on")
    private String sysUpdatedOn;
    @JsonProperty("sys_tags")
    private String sysTags;
    @JsonProperty("employment_end_date")
    private String employmentEndDate;
    @JsonProperty("location_type")
    private String locationType;
    @JsonProperty("number")
    private String number;
    @JsonProperty("sys_id")
    private String sysId;
    @JsonProperty("sys_updated_by")
    private String sysUpdatedBy;
    @JsonProperty("sys_created_on")
    private String sysCreatedOn;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("preferred_pronoun")
    private String preferredPronoun;
    @JsonProperty("legal_name")
    private String legalName;
    @JsonProperty("position_type")
    private String positionType;
    @JsonProperty("user")
    private LinkValueDTO user;
    @JsonProperty("sys_created_by")
    private String sysCreatedBy;
}
