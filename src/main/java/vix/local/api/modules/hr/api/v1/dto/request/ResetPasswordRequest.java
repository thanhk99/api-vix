package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}
