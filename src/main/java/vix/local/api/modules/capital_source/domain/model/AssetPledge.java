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
public class AssetPledge {
    private Long id;
    private String assetId;
    private String cusId; // Đối tác tín dụng
    private String contractNo; // Số HĐ tín dụng
    private String limitId; // Mã hạn mức
    private String pledgePlace; // Nơi cầm cố
    private LocalDate pledgeDate; // Ngày cầm cố
    private LocalDate endPledgeDate; // Ngày kết thúc HĐCC
    private BigDecimal pledgeQty; // Số lượng cầm cố
    private BigDecimal releasedQty; // Số lượng đã giải tỏa
    private BigDecimal price; // Giá (khi cầm cố)
    private BigDecimal marketValue; // Giá trị TS (pledgeQty * price)
    private BigDecimal haircutRate; // Haircut
    private BigDecimal collateralValue; // Giá trị TSBĐ
    private String pledgeContractNo; // Số HĐ/thỏa thuận cầm cố
    private String fileUrl; // File chứng từ
    private String note;
    private String status; // PENDING / APPROVED / REJECTED / PARTIALLY_RELEASED / RELEASED
    private String rejectReason;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public void validateForCreate() {
        if (this.assetId == null || this.assetId.trim().isEmpty()) {
            throw new AssetException("Mã tài sản không được để trống");
        }
        if (this.cusId == null || this.cusId.trim().isEmpty()) {
            throw new AssetException("Đối tác tín dụng không được để trống");
        }
        if (this.contractNo == null || this.contractNo.trim().isEmpty()) {
            throw new AssetException("Số HĐ tín dụng không được để trống");
        }
        if (this.limitId == null || this.limitId.trim().isEmpty()) {
            throw new AssetException("Mã hạn mức không được để trống");
        }
        if (this.pledgePlace == null || this.pledgePlace.trim().isEmpty()) {
            throw new AssetException("Nơi cầm cố không được để trống");
        }
        if (this.pledgeDate == null) {
            throw new AssetException("Ngày cầm cố không được để trống");
        }
        if (this.endPledgeDate == null) {
            throw new AssetException("Ngày kết thúc cầm cố không được để trống");
        }
        // Cho phép pledgeDate ở quá khứ nếu nhập bổ sung, nhưng thường > now. Ở đây nới lỏng hoặc check tùy yêu cầu.
        if (this.endPledgeDate.isBefore(this.pledgeDate)) {
            throw new AssetException("Ngày kết thúc cầm cố phải sau hoặc bằng ngày cầm cố");
        }
        if (this.pledgeQty == null || this.pledgeQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng cầm cố phải lớn hơn 0");
        }
    }

    public void calculateValues() {
        if (this.pledgeQty != null && this.price != null) {
            this.marketValue = this.pledgeQty.multiply(this.price);
            if (this.haircutRate != null) {
                BigDecimal multiplier = BigDecimal.ONE.subtract(this.haircutRate);
                this.collateralValue = this.marketValue.multiply(multiplier);
            } else {
                this.collateralValue = this.marketValue;
            }
        }
    }

    public BigDecimal getRemainingQty() {
        BigDecimal qty = this.pledgeQty != null ? this.pledgeQty : BigDecimal.ZERO;
        BigDecimal rel = this.releasedQty != null ? this.releasedQty : BigDecimal.ZERO;
        return qty.subtract(rel);
    }

    public void approve(String approvedBy) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể duyệt cầm cố ở trạng thái PENDING");
        }
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String approvedBy, String reason) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể từ chối cầm cố ở trạng thái PENDING");
        }
        this.status = "REJECTED";
        this.approvedBy = approvedBy;
        this.rejectReason = reason;
        this.approvedAt = LocalDateTime.now();
    }

    public void applyRelease(BigDecimal releaseQty) {
        if (releaseQty == null || releaseQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng giải tỏa phải > 0");
        }
        if (releaseQty.compareTo(getRemainingQty()) > 0) {
            throw new AssetException("Số lượng giải tỏa (" + releaseQty + ") vượt quá số lượng cầm cố còn lại (" + getRemainingQty() + ")");
        }
        
        if (this.releasedQty == null) this.releasedQty = BigDecimal.ZERO;
        this.releasedQty = this.releasedQty.add(releaseQty);
        
        if (this.releasedQty.compareTo(this.pledgeQty) >= 0) {
            this.status = "RELEASED";
        } else {
            this.status = "PARTIALLY_RELEASED";
        }
    }
}
