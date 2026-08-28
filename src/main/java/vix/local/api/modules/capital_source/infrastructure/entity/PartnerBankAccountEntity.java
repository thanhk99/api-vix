package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "capital_partner_bank_account", schema = "capital_source")
@Data
public class PartnerBankAccountEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    private String branch;

    @Column(name = "citad_code")
    private String citadCode;
    private String purpose;
    private String status;
    
    @Column(name = "account_type")
    private String accountType;

    @Column(name = "open_place")
    private String openPlace;

    @Column(name = "depository_member_no")
    private String depositoryMemberNo;

    @Column(name = "trading_gateway")
    private String tradingGateway;

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
