package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assets", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "asset_id", unique = true, nullable = false)
    private String assetId;

    @Column(name = "asset_type", nullable = false)
    private String assetType;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "issuer_code")
    private String issuerCode;

    @Column(name = "par_value")
    private BigDecimal parValue;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "haircut_rate")
    private BigDecimal haircutRate;

    @Column(name = "total_quantity")
    private BigDecimal totalQuantity;

    @Column(name = "avail_quantity")
    private BigDecimal availQuantity;

    @Column(name = "pledged_quantity")
    private BigDecimal pledgedQuantity;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "call_date")
    private LocalDate callDate;

    @Column(name = "coupon_type")
    private String couponType;

    @Column(name = "coupon_rate")
    private BigDecimal couponRate;

    @Column(name = "interest_pay_term")
    private String interestPayTerm;

    @Column(name = "note")
    private String note;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (totalQuantity == null) totalQuantity = BigDecimal.ZERO;
        if (availQuantity == null) availQuantity = BigDecimal.ZERO;
        if (pledgedQuantity == null) pledgedQuantity = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}