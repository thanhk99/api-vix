package vix.local.api.modules.capital_source.api.v1.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class GlobalCreditLimitRequestDto {
    private UUID partnerId;

    // Contract details (if contractId is null, create new contract)
    private UUID contractId;
    private String contractNo;
    private String contractType;
    private BigDecimal contractTotalLimit;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String contractPurpose;

    // Limit details
    private String limitId;
    private String poolName;
    private String currency;
    private String poolType;
    private BigDecimal creditRatio;
    private String purpose;
    private BigDecimal totalPool;
    private LocalDate startDate;
    private LocalDate endDate;
}
