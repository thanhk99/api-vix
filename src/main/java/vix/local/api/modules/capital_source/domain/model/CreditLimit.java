package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreditLimit {
    private UUID id;
    private UUID partnerId; // ID của đối tác liên quan
    private String limitId; // Mã hạn mức
    private String poolName; // Tên hạn mức
    private String currency; // Đơn vị tiền tệ
    private String poolType; // Loại hạn mức
    private BigDecimal totalPool; // Hạn mức tổng
    private BigDecimal usedPool; // Tổng hạn mức đã sử dụng
    private BigDecimal remainPool; // Tổng hạn mức còn lại
    private LocalDate startDate; // Ngày hiệu lực
    private LocalDate endDate; // Ngày hết hạn
    private String status; // Trạng thái

    public void validateCreditLimit() {
        if (this.totalPool == null ||
                this.totalPool.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CreditLimitException("Hạn mức phải lớn hơn 0");
        }

        if (this.startDate == null) {
            throw new CreditLimitException("Ngày hiệu lực không được để trống");
        }

        if (this.endDate != null &&
                this.startDate.isAfter(this.endDate)) {
            throw new CreditLimitException("Ngày hết hạn phải sau ngày hiệu lực");
        }
    }

    public void updateLimitAmount(BigDecimal newTotalPool) {
        if (newTotalPool == null || newTotalPool.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CreditLimitException("Hạn mức mới phải lớn hơn 0");
        }

        this.totalPool = newTotalPool;

        // Cập nhật remainPool nếu đã có giá trị
        if (this.usedPool != null && this.totalPool != null) {
            this.remainPool = this.totalPool.subtract(this.usedPool);
        }
    }

    public void calculateRemainPool() {
        if (this.totalPool != null && this.usedPool != null) {
            this.remainPool = this.totalPool.subtract(this.usedPool);
        }
    }
}