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
public class User {
    private UUID id;
    private String email;
    private String fullName;
    private String passwordHash;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID departmentId;
    private UserRole departmentRole;
    private UUID roleGroupId;

    public static User createNew(String email, String fullName, String passwordHash) {
        return User.builder()
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
