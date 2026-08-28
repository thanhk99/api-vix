package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "capital_partner_contact", schema = "capital_source")
@Data
public class PartnerContactEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    private String name;
    private String position;
    private String department;
    private String phone;
    private String email;
    private String role;

    @Column(name = "transaction_fee")
    private String transactionFee;

    private String note;
    private String status;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
    }
}
