package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PartnerPool {
    private UUID id;
    private String poolId;
    private String poolName;
    private BigDecimal totalPool;
    private BigDecimal usedPool;
    private BigDecimal remainPool;
    private UUID partnerId;
}