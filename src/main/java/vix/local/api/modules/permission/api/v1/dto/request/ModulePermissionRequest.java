package vix.local.api.modules.permission.api.v1.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ModulePermissionRequest {
    private String moduleName;
    private Boolean createPermission = false;
    private Boolean readPermission = false;
    private Boolean updatePermission = false;
    private Boolean deletePermission = false;
    private Boolean approvePermission = false;
    private UUID departmentId;
}