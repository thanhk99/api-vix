package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_transactions", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trans_type", nullable = false)
    private String transType; // BUY / SELL

    @Column(name = "counterparty_id", nullable = false)
    private String counterpartyId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "trade_amount", nullable = false)
    private BigDecimal tradeAmount;

    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "note")
    private String note;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
        if (feeAmount == null) feeAmount = BigDecimal.ZERO;
        if (currency == null) currency = "VND";
    }
}
