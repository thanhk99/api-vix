package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AssetException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class AssetTransaction {
    private Long id;
    private String transType; // BUY / SELL
    private String counterpartyId; // Đối tác
    private String assetId; // Mã tài sản
    private LocalDate tradeDate; // Ngày GD
    private LocalDate settlementDate; // Ngày thanh toán
    private BigDecimal quantity; // Số lượng
    private BigDecimal price; // Giá
    private BigDecimal tradeAmount; // Giá trị giao dịch (quantity * price)
    private BigDecimal feeAmount; // Phí
    private String currency; // Đơn vị tiền tệ
    private String referenceNo; // Số HĐ/chứng từ
    private String fileUrl; // File chứng từ
    private String note; // Ghi chú
    private String status; // PENDING / APPROVED / REJECTED
    private String rejectReason;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public void validateForCreate() {
        if (this.transType == null || !Set.of("BUY", "SELL").contains(this.transType.toUpperCase())) {
            throw new AssetException("Loại giao dịch phải là BUY hoặc SELL");
        }
        if (this.counterpartyId == null || this.counterpartyId.trim().isEmpty()) {
            throw new AssetException("Đối tác giao dịch không được để trống");
        }
        if (this.assetId == null || this.assetId.trim().isEmpty()) {
            throw new AssetException("Mã tài sản không được để trống");
        }
        if (this.tradeDate == null) {
            throw new AssetException("Ngày giao dịch không được để trống");
        }
        if (this.settlementDate == null) {
            throw new AssetException("Ngày thanh toán không được để trống");
        }
        if (this.settlementDate.isBefore(this.tradeDate)) {
            throw new AssetException("Ngày thanh toán phải sau hoặc bằng ngày giao dịch");
        }
        if (this.quantity == null || this.quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng giao dịch phải lớn hơn 0");
        }
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Giá giao dịch phải lớn hơn 0");
        }
    }

    public void calculateTradeAmount() {
        if (this.quantity != null && this.price != null) {
            this.tradeAmount = this.quantity.multiply(this.price);
        }
    }

    public void approve(String approvedBy) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể duyệt giao dịch ở trạng thái PENDING. Trạng thái hiện tại: " + this.status);
        }
        this.status = "APPROVED";
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String approvedBy, String reason) {
        if (!"PENDING".equals(this.status)) {
            throw new AssetException("Chỉ có thể từ chối giao dịch ở trạng thái PENDING. Trạng thái hiện tại: " + this.status);
        }
        this.status = "REJECTED";
        this.approvedBy = approvedBy;
        this.rejectReason = reason;
        this.approvedAt = LocalDateTime.now();
    }
}
