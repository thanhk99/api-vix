package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AssetException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Asset {
    private UUID id;
    private UUID partnerId; // ID của đối tác liên quan
    private UUID creditLimitId; // ID của HĐ hạn mức liên quan
    private String assetId; // Mã TSĐB
    private String assetType; // Loại TSĐB
    private String issuer; // Tổ chức phát hành
    private String issuerCode; // Mã TCPH
    private BigDecimal parValue; // Mệnh giá
    private LocalDate issueDate; // Ngày phát hành
    private LocalDate maturityDate; // Ngày đáo hạn
    private LocalDate callDate; // Ngày mua lại trước hạn
    private String couponType; // Loại lãi suất
    private BigDecimal couponRate; // Lãi suất coupon
    private BigDecimal interestPayTerm; // Kỳ trả lãi

    public void validateAsset() {
        if (this.assetId == null || this.assetId.isEmpty()) {
            throw new AssetException("Mã TSĐB không được để trống");
        }

        if (this.parValue != null && this.parValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Mệnh giá phải lớn hơn 0");
        }
    }
}