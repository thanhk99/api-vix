package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AssetResponseDto {
    private UUID id;
    private String assetId;
    private String assetType;
    private String issuer;
    private String issuerCode;
    private BigDecimal parValue;
    private LocalDate issueDate;
    private LocalDate maturityDate;
    private LocalDate callDate;
    private String couponType;
    private BigDecimal couponRate;
    private BigDecimal interestPayTerm;
}