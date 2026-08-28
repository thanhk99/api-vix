package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitHistoryResponseDto {
    private UUID id;
    private UUID creditLimitId;
    private String contactNo;
    private String limitType;
    private String branchCusId;
    private String cusName;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal initialLimit;
    private BigDecimal increaseAmount;
    private BigDecimal decreaseAmount;
    private BigDecimal remainLimit;
    private String reason;
    private String referenceId;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private String createdBy;
}
