package com.cag.servicenow_integration.controller;

import com.cag.servicenow_integration.constants.Constants;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.response.ServiceNowResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/servicenow/v1")
public class ServicenowController {
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;

    private final SuccessFactorsService successFactorsService;

    public ServicenowController(SuccessFactorsService successFactorsService) {
        this.successFactorsService = successFactorsService;
    }

    @GetMapping("/employee-profiles")
    public ResponseEntity<PaginatedResponse<EmployeeProfileDTO>> getEmployeeProfiles(
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit) {
        int sanitizedSkip = skip == null || skip < 0 ? 0 : skip;
        int sanitizedLimit = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        PaginatedResponse<EmployeeProfileDTO> response = successFactorsService.getEmployeeProfiles(sanitizedSkip, sanitizedLimit);
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
            .contentType(Constants.MEDIA_TYPE_CSV) // The Content-Type header tells clients how to handle the response
            .header(Constants.CONTENT_DISPOSITION_HEADER, String.format(Constants.CONTENT_DISPOSITION_FILENAME_TEMPLATE, id)) // Add this if wanting to download it as file on browser
            .body(CsvUtils.toCsv(employeeProfile));
    }
}

