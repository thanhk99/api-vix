package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AssetException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AssetPledgeRelease {
    private Long id;
    private Long pledgeId;
    private String assetId;
    private String contractNo;
    private String limitId;
    private String cusId;
    private BigDecimal releaseQty;
    private BigDecimal releaseValue;
    private LocalDate releaseDate;
    private String reason;
    private String note;
    private String fileUrl;
    private Boolean isExceptionApproved;
    private String exceptionApprover;
    private String exceptionReason;
    private String status;
    private String rejectReason;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public void validateForCreate(BigDecimal pledgeRemainingQty) {
        if (this.releaseQty == null || this.releaseQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng giải tỏa phải lớn hơn 0");
        }
        if (this.releaseDate == null) {
            this.releaseDate = LocalDate.now();
        }
        if (pledgeRemainingQty == null || this.releaseQty.compareTo(pledgeRemainingQty) > 0) {
            throw new AssetException("Số lượng giải tỏa (" + this.releaseQty + ") vượt quá số lượng cầm cố còn lại (" + (pledgeRemainingQty == null ? 0 : pledgeRemainingQty) + ")");
        }
    }

    public void approve(String approvedBy) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể duyệt giải tỏa ở trạng thái PENDING");
        }
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String approvedBy, String reason) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể từ chối giải tỏa ở trạng thái PENDING");
        }
        this.status = "REJECTED";
        this.approvedBy = approvedBy;
        this.rejectReason = reason;
        this.approvedAt = LocalDateTime.now();
    }
}
