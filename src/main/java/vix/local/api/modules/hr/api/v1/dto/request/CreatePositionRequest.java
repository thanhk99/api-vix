package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePositionRequest {
    @NotBlank(message = "Tên chức danh không được để trống")
    private String name;

    @NotBlank(message = "Mã chức danh không được để trống")
    private String code;

    private String description;
}
