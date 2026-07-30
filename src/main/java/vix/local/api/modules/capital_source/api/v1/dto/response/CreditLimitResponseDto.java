package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreditLimitResponseDto {
    private UUID id;
    private String limitId;
    private String poolName;
    private String currency;
    private String poolType;
    private BigDecimal totalPool;
    private BigDecimal usedPool;
    private BigDecimal remainPool;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}