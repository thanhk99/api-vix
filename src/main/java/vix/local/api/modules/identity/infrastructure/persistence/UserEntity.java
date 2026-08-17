package vix.local.api.modules.identity.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "shared", name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UUID departmentId;

    @Enumerated(EnumType.STRING)
    private UserRole departmentRole;

    private UUID roleGroupId;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
