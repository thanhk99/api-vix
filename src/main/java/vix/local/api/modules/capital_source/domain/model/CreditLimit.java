package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreditLimit {
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_DELETED = "DELETED";

    private UUID id;
    private UUID partnerId;
    private UUID parentId;
    private String limitId; // Mã hạn mức
    private String poolName; // Tên hạn mức
    private String currency; // Đơn vị tiền tệ
    private String poolType; // Loại hạn mức
    private String contactNo; // Số hợp đồng
    private BigDecimal creditRatio; // TL tài trợ/PA vay
    private String purpose; // Mục đích vay vốn
    private BigDecimal totalPool; // Hạn mức tổng
    private BigDecimal usedPool; // Tổng hạn mức đã sử dụng
    private BigDecimal remainPool; // Tổng hạn mức còn lại
    private LocalDate startDate; // Ngày hiệu lực
    private LocalDate endDate; // Ngày hết hạn
    private String status; // Trạng thái
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;

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
        if (STATUS_DELETED.equals(this.status)) {
            throw new CreditLimitException("Không thể cập nhật hạn mức đã xoá");
        }
        
        if (newTotalPool == null || newTotalPool.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CreditLimitException("Hạn mức mới phải lớn hơn 0");
        }

        this.totalPool = newTotalPool;

        // Cập nhật remainPool nếu đã có giá trị
        if (this.usedPool != null && this.totalPool != null) {
            this.remainPool = this.totalPool.subtract(this.usedPool);
        }
        
        // Reset trạng thái về chờ duyệt khi có cập nhật
        this.status = STATUS_PENDING_APPROVAL;
    }

    public void calculateRemainPool() {
        if (this.totalPool != null && this.usedPool != null) {
            this.remainPool = this.totalPool.subtract(this.usedPool);
        }
    }
    
    public void markAsApproved(UUID approverId) {
        if (STATUS_DELETED.equals(this.status)) {
            throw new CreditLimitException("Không thể duyệt hạn mức đã xoá");
        }
        if (STATUS_APPROVED.equals(this.status)) {
            throw new CreditLimitException("Hạn mức đã được duyệt");
        }
        this.status = STATUS_APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
    }
    
    public void markAsDeleted() {
        if (STATUS_DELETED.equals(this.status)) {
            throw new CreditLimitException("Hạn mức đã được xoá");
        }
        this.status = STATUS_DELETED;
    }
    
    public void resetToPending() {
        this.status = STATUS_PENDING_APPROVAL;
    }
}