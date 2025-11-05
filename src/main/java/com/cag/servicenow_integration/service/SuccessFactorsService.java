package com.cag.servicenow_integration.service;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;
import com.cag.servicenow_integration.response.PaginatedResponse;
import java.util.List;

public interface SuccessFactorsService {
    EmployeeProfileDTO getEmployeeProfile(String id);
    
    /**
     * Get employee profiles with pagination (legacy method)
     * @deprecated Use getEmployeeProfilesPaginated instead
     */
    @Deprecated
    List<EmployeeProfileDTO> getEmployeeProfiles(Integer page, Integer size);
    
    /**
     * Get employee profiles with skip/limit pagination and metadata
     * @param skip Number of records to skip
     * @param limit Maximum number of records to return
     * @return Paginated response with data and metadata
     */
    PaginatedResponse<EmployeeProfileDTO> getEmployeeProfilesPaginated(Integer skip, Integer limit);
    
    /**
     * Get total count of employee profiles
     * @return Total number of employee profiles available
     */
    Long getTotalEmployeeProfilesCount();
}
