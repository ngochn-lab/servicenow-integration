package com.cag.servicenow_integration.controller;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.ServiceNowResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/servicenow/v1")
public class ServicenowController {
    private final SuccessFactorsService successFactorsService;

    public ServicenowController(SuccessFactorsService successFactorsService) {
        this.successFactorsService = successFactorsService;
    }

    @GetMapping("/employee-profiles")
    public ResponseEntity<?> getEmployeeProfiles(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Page<EmployeeProfileDTO> employeeProfiles = successFactorsService.getEmployeeProfiles(PageRequest.of(page, size));
        ServiceNowResponse response = new ServiceNowResponse();
        response.setResult(employeeProfiles.getContent());
        return ResponseEntity.ok(response);
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
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            // Add this if wanting to download it as file on browser
            .header("Content-Disposition", "attachment; filename=\"employee_profile_" + id + ".csv\"")
            .body(CsvUtils.toCsv(employeeProfile));
    }
}

