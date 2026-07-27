package vix.local.api.modules.permission.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroup {
    private UUID id;
    private UUID deptId;
    private String name;
    private String description;
    private boolean isActive;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<RoleGroupPermission> permissions;
}
