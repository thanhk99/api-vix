package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import vix.local.api.modules.hr.domain.model.Gender;
import vix.local.api.modules.identity.domain.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateEmployeeRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    private UUID positionId;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private String address;

    private String idCardNumber;
    private LocalDate idCardIssuedDate;
    private String idCardIssuedPlace;

    private LocalDate joinDate;
    private String avatarUrl;

    private UserRole role;
}
