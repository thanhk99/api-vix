package vix.local.api.modules.permission.api.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignUsersRequest {
    @NotNull(message = "Danh sách user không được null")
    private List<UUID> userIds;
}
