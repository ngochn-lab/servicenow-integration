package com.cag.servicenow_integration.controller;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.ServiceNowResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servicenow/v1")
public class ServicenowController {
    private final SuccessFactorsService successFactorsService;

    public ServicenowController(SuccessFactorsService successFactorsService) {
        this.successFactorsService = successFactorsService;
    }

    @GetMapping("/employee-profile/{id}")
    public ResponseEntity<?> getEmployeeProfile(@PathVariable String id) {
        EmployeeProfileDTO employeeProfile = successFactorsService.getEmployeeProfile(id);
        ServiceNowResponse serviceNowResponse = new ServiceNowResponse();
        serviceNowResponse.setResult(employeeProfile);
        return ResponseEntity.ok(serviceNowResponse);
    }

    @GetMapping("/employee-profile/{id}/csv")
    public ResponseEntity<String> getEmployeeProfileCsv(@PathVariable String id) {
        EmployeeProfileDTO employeeProfile = successFactorsService.getEmployeeProfile(id);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/csv")
                // Add this if wanting to download it as file
//                .header("Content-Disposition", "attachment; filename=\"employee_profile_" + id + ".csv\"")
                .body(toCsv(employeeProfile));
    }

    public String toCsv(EmployeeProfileDTO dto) {
        StringBuilder sb = new StringBuilder();
        // Header
        sb.append("background_banner,employment_start_date,short_bio,sys_mod_count,work_mobile,work_phone,sys_updated_on,sys_tags,employment_end_date,location_type,number,sys_id,sys_updated_by,sys_created_on,nickname,preferred_pronoun,legal_name,position_type,user,sys_created_by\n");
        // Data
        sb.append(escapeCsv(dto.getBackgroundBanner())).append(",");
        sb.append(escapeCsv(dto.getEmploymentStartDate())).append(",");
        sb.append(escapeCsv(dto.getShortBio())).append(",");
        sb.append(escapeCsv(dto.getSysModCount())).append(",");
        sb.append(escapeCsv(dto.getWorkMobile())).append(",");
        sb.append(escapeCsv(dto.getWorkPhone())).append(",");
        sb.append(escapeCsv(dto.getSysUpdatedOn())).append(",");
        sb.append(escapeCsv(dto.getSysTags())).append(",");
        sb.append(escapeCsv(dto.getEmploymentEndDate())).append(",");
        sb.append(escapeCsv(dto.getLocationType())).append(",");
        sb.append(escapeCsv(dto.getNumber())).append(",");
        sb.append(escapeCsv(dto.getSysId())).append(",");
        sb.append(escapeCsv(dto.getSysUpdatedBy())).append(",");
        sb.append(escapeCsv(dto.getSysCreatedOn())).append(",");
        sb.append(escapeCsv(dto.getNickname())).append(",");
        sb.append(escapeCsv(dto.getPreferredPronoun())).append(",");
        sb.append(escapeCsv(dto.getLegalName())).append(",");
        sb.append(escapeCsv(dto.getPositionType())).append(",");
        sb.append(escapeCsv(dto.getUser() != null ? dto.getUser().getValue() : "")).append(",");
        sb.append(escapeCsv(dto.getSysCreatedBy())).append("\n");
        return sb.toString();
    }

    // Simple CSV escaping
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}

