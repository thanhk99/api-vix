package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_limits", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "contract_id")
    private UUID contractId;

    @Column(name = "limit_id")
    private String limitId;  // Mã hạn mức

    @Column(name = "pool_name")
    private String poolName;  // Tên hạn mức

    @Column(name = "currency")
    private String currency;  // Đơn vị tiền tệ

    @Column(name = "pool_type")
    private String poolType;  // Loại hạn mức

    @Column(name = "credit_ratio")
    private BigDecimal creditRatio; // TL tài trợ/PA vay

    @Column(name = "purpose")
    private String purpose; // Mục đích vay vốn


    @Column(name = "total_pool")
    private BigDecimal totalPool;  // Hạn mức tổng

    @Column(name = "used_pool")
    private BigDecimal usedPool;  // Tổng hạn mức đã sử dụng

    @Column(name = "remain_pool")
    private BigDecimal remainPool;  // Tổng hạn mức còn lại

    @Column(name = "start_date")
    private LocalDate startDate;  // Ngày hiệu lực

    @Column(name = "end_date")
    private LocalDate endDate;  // Ngày hết hạn

    private String status;  // Trạng thái

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "approved_by")
    private java.util.UUID approvedBy;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = java.time.LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}