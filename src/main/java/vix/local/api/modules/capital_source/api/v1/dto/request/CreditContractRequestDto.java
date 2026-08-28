package vix.local.api.modules.capital_source.api.v1.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreditContractRequestDto {
    private String contractNo;
    private BigDecimal totalLimit;
    private String purpose;
    private LocalDate startDate;
    private LocalDate endDate;
}
