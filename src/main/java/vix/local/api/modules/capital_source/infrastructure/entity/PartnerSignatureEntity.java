package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "partner_signatures", schema = "capital_source")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSignatureEntity {

    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "sign_file_name")
    private String signFileName;

    @Column(name = "sign_type")
    private String signType;

    @Column(name = "description")
    private String description;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
