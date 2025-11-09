package com.cag.servicenow_integration.dto.servicenow;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeProfileDTO {
    @JsonProperty("background_banner")
    private String backgroundBanner;

    @JsonProperty("employment_start_date")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "employment_start_date must be in the format YYYY-MM-DD")
    private String employmentStartDate;

    @JsonProperty("short_bio")
    @Size(max = 500, message = "short_bio must not exceed 500 characters")
    private String shortBio;

    @JsonProperty("sys_mod_count")
    @Pattern(regexp = "^\\d+$", message = "sys_mod_count must be a non-negative integer")
    private String sysModCount;

    @JsonProperty("work_mobile")
    @Size(max = 15, message = "work_mobile must not exceed 15 characters")
    private String workMobile;

    @JsonProperty("work_phone")
    @Size(max = 15, message = "work_phone must not exceed 15 characters")
    private String workPhone;

    @JsonProperty("sys_updated_on")
    private String sysUpdatedOn;

    @JsonProperty("sys_tags")
    private String sysTags;

    @JsonProperty("employment_end_date")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "employment_end_date must be in the format YYYY-MM-DD")
    private String employmentEndDate;

    @JsonProperty("location_type")
    private String locationType;

    @JsonProperty("number")
    @NotBlank
    private String number;

    @JsonProperty("sys_id")
    @NotBlank
    private String sysId;

    @JsonProperty("sys_updated_by")
    private String sysUpdatedBy;

    @JsonProperty("sys_created_on")
    private String sysCreatedOn;

    @JsonProperty("nickname")
    @Size(max = 25, message = "nickname must not exceed 25 characters")
    private String nickname;

    @JsonProperty("preferred_pronoun")
    private String preferredPronoun;

    @JsonProperty("legal_name")
    @NotBlank
    private String legalName;

    @JsonProperty("position_type")
    private String positionType;

    @JsonProperty("user")
    @NotNull
    private LinkValueDTO user;

    @JsonProperty("sys_created_by")
    private String sysCreatedBy;
}
