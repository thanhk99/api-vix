package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_limit_histories", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitHistoryEntity {
    @Id
    private UUID id;

    @Column(name = "credit_limit_id", nullable = false)
    private UUID creditLimitId;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "pre_total_pool")
    private BigDecimal preTotalPool;

    @Column(name = "pre_used_pool")
    private BigDecimal preUsedPool;

    @Column(name = "pre_remain_pool")
    private BigDecimal preRemainPool;

    @Column(name = "new_total_pool")
    private BigDecimal newTotalPool;

    @Column(name = "new_used_pool")
    private BigDecimal newUsedPool;

    @Column(name = "new_remain_pool")
    private BigDecimal newRemainPool;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
    }
}
