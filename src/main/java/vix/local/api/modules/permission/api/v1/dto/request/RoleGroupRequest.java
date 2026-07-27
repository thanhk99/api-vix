package vix.local.api.modules.permission.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleGroupRequest {
    @NotBlank(message = "Tên nhóm quyền không được bỏ trống")
    private String name;
    
    private String description;
    
    private boolean isActive = true;
    
    private List<PermissionDto> permissions;
}
