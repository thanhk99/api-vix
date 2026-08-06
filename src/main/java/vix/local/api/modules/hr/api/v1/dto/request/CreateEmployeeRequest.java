package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vix.local.api.modules.hr.domain.model.Gender;
import vix.local.api.modules.identity.domain.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateEmployeeRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotNull(message = "ID phòng ban không được để trống")
    private UUID departmentId;

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
