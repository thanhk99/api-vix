package vix.local.api.modules.hr.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SetManagerRequest {
    @NotNull(message = "ID trưởng phòng không được để trống")
    private UUID managerId;
}
