package vix.local.api.modules.hr.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployeeListItemResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String employeeCode;
    private UUID departmentId;
    private UUID positionId;
    private String status;
    private String avatarUrl;
}
