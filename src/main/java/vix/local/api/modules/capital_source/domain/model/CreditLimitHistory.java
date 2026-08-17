package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreditLimitHistory {
    private UUID id;
    private UUID creditLimitId; // ID hợp đồng hạn mức
    private String transactionType; // Loại giao dịch
    private BigDecimal amount; // Số tiền thay đổi
    
    private BigDecimal preTotalPool; // Hạn mức tổng trước thay đổi
    private BigDecimal preUsedPool; // Hạn mức đã dùng trước thay đổi
    private BigDecimal preRemainPool; // Hạn mức còn lại trước thay đổi
    
    private BigDecimal newTotalPool; // Hạn mức tổng sau thay đổi
    private BigDecimal newUsedPool; // Hạn mức đã dùng sau thay đổi
    private BigDecimal newRemainPool; // Hạn mức còn lại sau thay đổi
    
    private LocalDateTime transactionDate; // Ngày giao dịch
    private String referenceId; // Mã tham chiếu (KUNN, Asset ID, ...)
    private LocalDateTime createdAt;
    private UUID createdBy;
}
