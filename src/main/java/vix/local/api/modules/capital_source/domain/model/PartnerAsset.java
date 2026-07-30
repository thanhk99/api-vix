package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PartnerAsset {
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