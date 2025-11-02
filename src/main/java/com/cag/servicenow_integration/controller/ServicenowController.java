package com.cag.servicenow_integration.controller;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.request.ServiceNowRequest;
import com.cag.servicenow_integration.response.ServiceNowResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
