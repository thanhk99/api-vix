package vix.local.api.modules.hr.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vix.local.api.modules.hr.domain.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "shared", name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrUserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String status;

    // HR fields
    @Column(unique = true)
    private String employeeCode;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String address;

    // CCCD
    private String idCardNumber;
    private LocalDate idCardIssuedDate;
    private String idCardIssuedPlace;

    // Department & Position
    private UUID departmentId;
    private UUID positionId;

    // Employment dates
    private LocalDate joinDate;
    private LocalDate terminateDate;

    @Column(columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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
