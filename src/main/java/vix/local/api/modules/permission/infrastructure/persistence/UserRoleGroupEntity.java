package vix.local.api.modules.permission.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "shared", name = "user_role_groups")
@IdClass(UserRoleGroupId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleGroupEntity {

    @Id
    @Column(nullable = false)
    private UUID userId;

    @Id
    @Column(nullable = false)
    private UUID roleGroupId;

    @Column(nullable = false)
    private UUID deptId;

    private UUID assignedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
    }
}
