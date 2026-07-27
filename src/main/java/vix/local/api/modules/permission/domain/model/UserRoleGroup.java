package vix.local.api.modules.permission.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleGroup {
    private UUID userId;
    private UUID roleGroupId;
    private UUID deptId;
    private UUID assignedBy;
    private LocalDateTime assignedAt;
}
