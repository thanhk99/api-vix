package vix.local.api.modules.permission.api.v1.dto.response;

import lombok.Data;
import vix.local.api.modules.permission.api.v1.dto.request.PermissionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RoleGroupResponse {
    private UUID id;
    private UUID deptId;
    private String name;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<PermissionDto> permissions;
}
