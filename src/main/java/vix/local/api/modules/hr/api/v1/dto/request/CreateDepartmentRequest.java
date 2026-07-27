package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateDepartmentRequest {
    @NotBlank(message = "Tên phòng ban không được để trống")
    private String name;

    @NotBlank(message = "Mã phòng ban không được để trống")
    private String code;

    private String description;
    private UUID managerId;
}
