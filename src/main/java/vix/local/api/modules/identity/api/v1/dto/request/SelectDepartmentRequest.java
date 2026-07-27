package vix.local.api.modules.identity.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SelectDepartmentRequest {
    @NotNull(message = "deptId không được null")
    private UUID deptId;
}
