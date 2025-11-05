package com.cag.servicenow_integration.controller;

import com.cag.servicenow_integration.config.TimeoutConfiguration;
import com.cag.servicenow_integration.dto.PaginationRequest;
import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.PaginatedResponse;
import com.cag.servicenow_integration.response.ServiceNowResponse;
import com.cag.servicenow_integration.service.SuccessFactorsService;
import com.cag.servicenow_integration.utils.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/servicenow/v1")
public class ServicenowController {
    private final SuccessFactorsService successFactorsService;
    private final TimeoutConfiguration timeoutConfiguration;

    public ServicenowController(SuccessFactorsService successFactorsService, 
                                TimeoutConfiguration timeoutConfiguration) {
        this.successFactorsService = successFactorsService;
        this.timeoutConfiguration = timeoutConfiguration;
    }

    /**
     * Get employee profiles with skip/limit pagination.
     * This endpoint supports pagination to prevent data overload and manage timeouts effectively.
     * 
     * @param skip Number of records to skip from the start (offset)
     * @param limit Maximum number of records to return (page size)
     * @return Paginated response with employee profiles and metadata
     */
    @GetMapping("/employee-profiles")
    public ResponseEntity<PaginatedResponse<EmployeeProfileDTO>> getEmployeeProfiles(
            @RequestParam(required = false, defaultValue = "0") Integer skip,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        
        long startTime = System.currentTimeMillis();
        
        // Create and sanitize pagination request
        PaginationRequest paginationRequest = PaginationRequest.builder()
                .skip(skip)
                .limit(limit)
                .build();
        paginationRequest.sanitize();
        
        log.info("Processing employee profiles request with skip={}, limit={}", 
                paginationRequest.getSkip(), paginationRequest.getLimit());
        
        try {
            // Get paginated data from service
            PaginatedResponse<EmployeeProfileDTO> response = successFactorsService.getEmployeeProfilesPaginated(
                    paginationRequest.getSkip(), 
                    paginationRequest.getLimit());
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Add warnings if necessary
            List<String> warnings = new ArrayList<>();
            if (timeoutConfiguration.exceedsWarningThreshold(processingTime)) {
                warnings.add(String.format("Request processing time (%d ms) exceeded warning threshold", 
                        processingTime));
            }
            if (paginationRequest.getLimit() > 100) {
                warnings.add("Large page size requested. Consider using smaller limit for better performance.");
            }
            
            // Set response status
            response.setStatus(PaginatedResponse.ResponseStatus.success(processingTime, warnings));
            
            log.info("Successfully processed employee profiles request in {} ms", processingTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing employee profiles request", e);
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Return error response with empty data
            PaginatedResponse<EmployeeProfileDTO> errorResponse = PaginatedResponse.<EmployeeProfileDTO>builder()
                    .data(new ArrayList<>())
                    .pagination(PaginatedResponse.PaginationMetadata.calculate(
                            paginationRequest.getSkip(), 
                            paginationRequest.getLimit(), 
                            0, 
                            0))
                    .status(PaginatedResponse.ResponseStatus.builder()
                            .code(500)
                            .message("Internal server error: " + e.getMessage())
                            .timestamp(System.currentTimeMillis())
                            .processingTimeMs(processingTime)
                            .build())
                    .build();
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Legacy endpoint for backward compatibility using page/size parameters.
     * Internally converts to skip/limit.
     * 
     * @deprecated Use /employee-profiles with skip/limit parameters instead
     */
    @GetMapping("/employee-profiles/legacy")
    @Deprecated
    public ResponseEntity<?> getEmployeeProfilesLegacy(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size) {
        
        // Convert page/size to skip/limit
        int skip = page * size;
        return getEmployeeProfiles(skip, size);
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

