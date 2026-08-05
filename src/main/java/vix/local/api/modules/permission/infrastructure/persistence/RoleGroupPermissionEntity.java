package vix.local.api.modules.permission.infrastructure.persistence;

import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(schema = "shared", name = "role_group_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroupPermissionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID roleGroupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private ResourceCode resource;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "shared", name = "role_group_permission_actions", joinColumns = @JoinColumn(name = "permission_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Set<ActionCode> actions;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
    }
}
