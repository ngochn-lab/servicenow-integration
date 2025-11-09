package com.cag.servicenow_integration.utils;

import com.cag.servicenow_integration.dto.servicenow.EmployeeProfileDTO;


public class CsvUtils {
    public static String toCsv(EmployeeProfileDTO dto) {
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
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
