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
@Table(name = "asset_pledges", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetPledgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "cus_id", nullable = false)
    private String cusId;

    @Column(name = "contract_no", nullable = false)
    private String contractNo;

    @Column(name = "limit_id", nullable = false)
    private String limitId;

    @Column(name = "pledge_place", nullable = false)
    private String pledgePlace;

    @Column(name = "pledge_date", nullable = false)
    private LocalDate pledgeDate;

    @Column(name = "end_pledge_date", nullable = false)
    private LocalDate endPledgeDate;

    @Column(name = "pledge_qty", nullable = false)
    private BigDecimal pledgeQty;

    @Column(name = "released_qty")
    private BigDecimal releasedQty;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "market_value")
    private BigDecimal marketValue;

    @Column(name = "haircut_rate")
    private BigDecimal haircutRate;

    @Column(name = "collateral_value")
    private BigDecimal collateralValue;

    @Column(name = "pledge_contract_no")
    private String pledgeContractNo;

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
        if (releasedQty == null) releasedQty = BigDecimal.ZERO;
    }
}
