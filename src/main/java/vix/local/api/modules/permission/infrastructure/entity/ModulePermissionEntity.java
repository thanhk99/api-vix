package vix.local.api.modules.permission.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "module_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModulePermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "create_permission", nullable = false)
    @Builder.Default
    private Boolean createPermission = false;

    @Column(name = "read_permission", nullable = false)
    @Builder.Default
    private Boolean readPermission = false;

    @Column(name = "update_permission", nullable = false)
    @Builder.Default
    private Boolean updatePermission = false;

    @Column(name = "delete_permission", nullable = false)
    @Builder.Default
    private Boolean deletePermission = false;

    @Column(name = "approve_permission", nullable = false)
    @Builder.Default
    private Boolean approvePermission = false;

    @Column(name = "department_id")
    private UUID departmentId;
}