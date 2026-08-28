package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "capital_credit_contract", schema = "capital_source")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "contract_no")
    private String contractNo;
    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "total_limit")
    private BigDecimal totalLimit;

    @Column(name = "used_limit")
    private BigDecimal usedLimit;

    @Column(name = "remain_limit")
    private BigDecimal remainLimit;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
