package vix.local.api.modules.identity.domain.model;

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
public class UserDepartment {
    private UUID id;
    private UUID userId;
    private UUID departmentId;
    private UserRole role;
    private boolean isPrimary;
    private LocalDateTime createdAt;
}
