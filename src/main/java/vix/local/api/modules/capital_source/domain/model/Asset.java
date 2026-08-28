package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AssetException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Asset {
    private UUID id;
    private String assetId; // Mã TSĐB
    private String assetType; // Loại TSĐB
    private String symbol; // Mã CK
    private String currency; // Đơn vị tiền tệ
    private String issuer; // Tổ chức phát hành
    private String issuerCode; // Mã TCPH
    private BigDecimal parValue; // Mệnh giá
    private BigDecimal marketPrice; // Giá thị trường
    private BigDecimal haircutRate; // Tỷ lệ haircut
    private BigDecimal totalQuantity; // Tổng số lượng
    private BigDecimal availQuantity; // Khả dụng
    private BigDecimal pledgedQuantity; // Cầm cố
    private LocalDate issueDate; // Ngày phát hành
    private LocalDate maturityDate; // Ngày đáo hạn
    private LocalDate callDate; // Ngày mua lại trước hạn
    private String couponType; // Loại lãi suất
    private BigDecimal couponRate; // Lãi suất coupon
    private String interestPayTerm; // Kỳ trả lãi
    private String note; // Ghi chú
    private String status; // Trạng thái
    private String createdBy;
    private String updatedBy;

    public void validateForCreate() {
        if (this.assetType == null || this.assetType.trim().isEmpty()) {
            throw new AssetException("Loại tài sản không được để trống");
        }
        if (this.issuer == null || this.issuer.trim().isEmpty()) {
            throw new AssetException("Tổ chức phát hành không được để trống");
        }
        if (this.currency == null || this.currency.trim().isEmpty()) {
            throw new AssetException("Đơn vị tiền tệ không được để trống");
        }
        if (this.parValue != null && this.parValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Mệnh giá phải lớn hơn 0");
        }
    }

    public void validateForUpdate(boolean hasTransactions) {
        if (hasTransactions) {
            throw new AssetException("Chỉ được phép sửa thông tin tài sản khi chưa phát sinh giao dịch");
        }
    }

    public void updateMarketPrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new AssetException("Giá thị trường không hợp lệ");
        }
        this.marketPrice = newPrice;
    }

    // 8.2 MUA
    public void applyBuy(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng mua phải > 0");
        }
        if (this.totalQuantity == null) this.totalQuantity = BigDecimal.ZERO;
        if (this.availQuantity == null) this.availQuantity = BigDecimal.ZERO;

        this.totalQuantity = this.totalQuantity.add(quantity);
        this.availQuantity = this.availQuantity.add(quantity);
        recalculateStatus();
    }

    // 8.2 BÁN (Validate)
    public void validateSell(BigDecimal quantity) {
        if (!Set.of("AVAILABLE", "PARTIALLY_RELEASED", "RELEASED", "PARTIALLY_PLEDGED").contains(this.status)) {
            throw new AssetException("Tài sản không ở trạng thái có thể bán. Trạng thái hiện tại: " + this.status);
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng bán phải > 0");
        }
        if (this.availQuantity == null || quantity.compareTo(this.availQuantity) > 0) {
            throw new AssetException("Số lượng bán vượt quá số lượng khả dụng. Khả dụng: " + (this.availQuantity == null ? 0 : this.availQuantity));
        }
    }

    // 8.2 BÁN (Áp dụng)
    public void applySell(BigDecimal quantity) {
        validateSell(quantity);
        this.totalQuantity = this.totalQuantity.subtract(quantity);
        this.availQuantity = this.availQuantity.subtract(quantity);
        recalculateStatus();
    }

    // 8.3 CẦM CỐ (Validate)
    public void validatePledge(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng cầm cố phải > 0");
        }
        if (this.availQuantity == null || quantity.compareTo(this.availQuantity) > 0) {
            throw new AssetException("Số lượng cầm cố vượt quá số lượng khả dụng. Khả dụng: " + (this.availQuantity == null ? 0 : this.availQuantity));
        }
    }

    // 8.3 CẦM CỐ (Áp dụng)
    public void applyPledge(BigDecimal quantity) {
        validatePledge(quantity);
        this.availQuantity = this.availQuantity.subtract(quantity);
        if (this.pledgedQuantity == null) this.pledgedQuantity = BigDecimal.ZERO;
        this.pledgedQuantity = this.pledgedQuantity.add(quantity);
        recalculateStatus();
    }

    // 8.4 GIẢI TỎA (Áp dụng)
    public void applyRelease(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AssetException("Số lượng giải tỏa phải > 0");
        }
        if (this.pledgedQuantity == null || quantity.compareTo(this.pledgedQuantity) > 0) {
            throw new AssetException("Số lượng giải tỏa vượt quá số lượng đang cầm cố toàn bộ tài sản. Cầm cố: " + (this.pledgedQuantity == null ? 0 : this.pledgedQuantity));
        }
        this.pledgedQuantity = this.pledgedQuantity.subtract(quantity);
        if (this.availQuantity == null) this.availQuantity = BigDecimal.ZERO;
        this.availQuantity = this.availQuantity.add(quantity);
        recalculateStatus();
    }

    // Đổi trạng thái tự động
    public void recalculateStatus() {
        BigDecimal total = this.totalQuantity != null ? this.totalQuantity : BigDecimal.ZERO;
        BigDecimal pledged = this.pledgedQuantity != null ? this.pledgedQuantity : BigDecimal.ZERO;
        BigDecimal avail = this.availQuantity != null ? this.availQuantity : BigDecimal.ZERO;

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "SOLD";
            return;
        }
        if (pledged.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "AVAILABLE";
            return;
        }
        if (avail.compareTo(BigDecimal.ZERO) == 0) {
            this.status = "PLEDGED";
            return;
        }
        this.status = "PARTIALLY_PLEDGED";
    }

    public void activate() {
        if ("INACTIVE".equals(this.status)) {
            recalculateStatus();
        }
    }

    public void deactivate() {
        if (this.pledgedQuantity != null && this.pledgedQuantity.compareTo(BigDecimal.ZERO) > 0) {
            throw new AssetException("Không thể ngừng sử dụng tài sản đang được cầm cố");
        }
        this.status = "INACTIVE";
    }

    public void markAsDeleted() {
        this.status = "DELETED";
    }
}