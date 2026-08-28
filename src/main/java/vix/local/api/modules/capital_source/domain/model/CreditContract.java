package vix.local.api.modules.capital_source.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditContract {
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PENDING_DELETE = "PENDING_DELETE";
    public static final String STATUS_DELETED = "DELETED";

    private UUID id;
    private UUID partnerId;
    private String contractNo;
    private String contractType;
    private BigDecimal totalLimit;
    private BigDecimal usedLimit;
    private BigDecimal remainLimit;
    private String purpose;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;

    public void initRemain() {
        if (this.usedLimit == null) this.usedLimit = BigDecimal.ZERO;
        if (this.totalLimit != null) {
            this.remainLimit = this.totalLimit.subtract(this.usedLimit);
        }
    }

    public void consume(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CreditLimitException("Số tiền tiêu hao tại Hợp đồng phải lớn hơn 0");
        }
        if (this.usedLimit == null) this.usedLimit = BigDecimal.ZERO;

        BigDecimal newUsed = this.usedLimit.add(amount);
        if (this.totalLimit != null && newUsed.compareTo(this.totalLimit) > 0) {
            throw new CreditLimitException("Số tiền giải ngân vượt quá Tổng hạn mức của Hợp đồng");
        }
        this.usedLimit = newUsed;
        initRemain();
    }

    public void release(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CreditLimitException("Số tiền hoàn lại tại Hợp đồng phải lớn hơn 0");
        }
        if (this.usedLimit == null) this.usedLimit = BigDecimal.ZERO;

        BigDecimal newUsed = this.usedLimit.subtract(amount);
        if (newUsed.compareTo(BigDecimal.ZERO) < 0) {
            newUsed = BigDecimal.ZERO;
        }
        this.usedLimit = newUsed;
        initRemain();
    }
    
    public void markAsPendingDelete() {
        if (STATUS_DELETED.equals(this.status)) {
            throw new CreditLimitException("Hợp đồng đã được xoá");
        }
        this.status = STATUS_PENDING_DELETE;
    }
}
