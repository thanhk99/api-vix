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
    private String departmentName;
    private String departmentCode;

    private UUID positionId;
    private String positionName;
    private String positionCode;

    private vix.local.api.modules.identity.domain.model.UserRole role;

    private String status;
    private String avatarUrl;
}
