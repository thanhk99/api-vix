package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreditContractResponseDto {
    private UUID id;
    private UUID partnerId;
    private String contractNo;
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
}
