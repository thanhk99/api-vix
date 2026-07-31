package vix.local.api.modules.permission.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModulePermission {

    private UUID userId;
    private UUID departmentId;
    private ModulePermission module;
    private List<String> permissions; // List of permission codes like ["C", "R", "U", "D", "A"]
}