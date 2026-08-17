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
@Table(name = "assets", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "credit_limit_id")
    private UUID creditLimitId;


    @Column(name = "asset_id")
    private String assetId;  // Mã TSĐB

    @Column(name = "asset_type")
    private String assetType;  // Loại TSĐB

    @Column(name = "issuer")
    private String issuer;  // Tổ chức phát hành

    @Column(name = "issuer_code")
    private String issuerCode;  // Mã TCPH

    @Column(name = "par_value")
    private BigDecimal parValue;  // Mệnh giá

    @Column(name = "issue_date")
    private LocalDate issueDate;  // Ngày phát hành

    @Column(name = "maturity_date")
    private LocalDate maturityDate;  // Ngày đáo hạn

    @Column(name = "call_date")
    private LocalDate callDate;  // Ngày mua lại trước hạn

    @Column(name = "coupon_type")
    private String couponType;  // Loại lãi suất

    @Column(name = "coupon_rate")
    private BigDecimal couponRate;  // Lãi suất coupon

    @Column(name = "interest_pay_term")
    private BigDecimal interestPayTerm;  // Kỳ trả lãi
}