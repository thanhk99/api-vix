package vix.local.api.modules.hr.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrUser {
    private UUID id;
    private String email;
    private String fullName;
    private String passwordHash;
    private String status; // ACTIVE, INACTIVE, TERMINATED

    // HR fields
    private String employeeCode;   // Rule: {DEPT_CODE}{3-digit sequence}, e.g. BGD001
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String address;

    // CCCD
    private String idCardNumber;
    private LocalDate idCardIssuedDate;
    private String idCardIssuedPlace;

    // Department & Position
    private UUID departmentId;
    private UUID positionId;

    // Employment dates
    private LocalDate joinDate;
    private LocalDate terminateDate;
    private String avatarUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method — Giám đốc
    public static HrUser createDirector(String email, String fullName, String passwordHash, UUID departmentId) {
        return HrUser.builder()
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordHash)
                .status("ACTIVE")
                .employeeCode("BGD001")
                .departmentId(departmentId)
                .joinDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Business method
    public void terminate() {
        this.status = "TERMINATED";
        this.terminateDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void transferTo(UUID newDeptId, String newEmployeeCode) {
        this.departmentId = newDeptId;
        this.employeeCode = newEmployeeCode;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = "INACTIVE";
        this.updatedAt = LocalDateTime.now();
    }
}
