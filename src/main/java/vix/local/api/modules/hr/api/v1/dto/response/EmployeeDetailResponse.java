package vix.local.api.modules.hr.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import vix.local.api.modules.hr.domain.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmployeeDetailResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String status;
    private String employeeCode;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String address;

    private String idCardNumber;
    private LocalDate idCardIssuedDate;
    private String idCardIssuedPlace;

    private UUID departmentId;
    private String departmentName;
    private String departmentCode;

    private UUID positionId;
    private String positionName;
    private String positionCode;

    private vix.local.api.modules.identity.domain.model.UserRole role;

    private LocalDate joinDate;
    private LocalDate terminateDate;
    private String avatarUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
