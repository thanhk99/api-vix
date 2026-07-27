package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class TransferDepartmentRequest {
    @NotNull(message = "ID phòng ban mới không được để trống")
    private UUID newDepartmentId;
}
