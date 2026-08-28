package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Kunn;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface KunnRepository {
    Kunn save(Kunn kunn);
    Optional<Kunn> findById(UUID id);
    List<Kunn> findAll();
    org.springframework.data.domain.Page<Kunn> findAll(org.springframework.data.domain.Pageable pageable);
    List<Kunn> findByLimitId(UUID limitId);
    List<Kunn> findByCusId(UUID cusId);
    List<Kunn> saveAll(List<Kunn> kunns);
    java.math.BigDecimal sumPendingLnAmtByLimitId(UUID limitId, UUID excludeKunnId);
    java.math.BigDecimal sumPendingLnAmtByContractId(UUID contractId, UUID excludeKunnId);
    java.math.BigDecimal sumPendingLnAmtByPartnerId(UUID partnerId, UUID excludeKunnId);
}
