package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotNull(message = "ID công ty không được để trống")
    private UUID companyId;

    @NotNull(message = "ID phòng ban không được để trống")
    private UUID departmentId;

    @NotBlank(message = "Role không được để trống")
    private String role;
}
