package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitResponseDto {
    private UUID id;
    private UUID partnerId;
    private String branchCusId;
    private String cusName;
    private String limitId;
    private String poolName;
    private String currency;
    private String poolType;
    private String contractIdStr;
    private BigDecimal creditRatio;
    private String purpose;
    private BigDecimal totalPool;
    private BigDecimal usedPool;
    private BigDecimal remainPool;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String approvedBy;
    private java.time.LocalDateTime approvedAt;
    
    private UUID contractId;
    private String contractNo;
    private boolean hasCollateral;
    private java.util.List<CreditLimitResponseDto> children;
}
