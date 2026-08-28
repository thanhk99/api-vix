package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KunnJpaRepository extends JpaRepository<KunnEntity, UUID> {
    java.util.List<KunnEntity> findByLimitId(UUID limitId);
    java.util.List<KunnEntity> findByCusId(UUID cusId);

    @Query("SELECT k FROM KunnEntity k WHERE k.status <> vix.local.api.modules.capital_source.domain.model.KunnStatus.DELETED OR k.status IS NULL")
    org.springframework.data.domain.Page<KunnEntity> findActiveKunns(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COALESCE(SUM(k.lnAmt), 0) FROM KunnEntity k WHERE k.status = vix.local.api.modules.capital_source.domain.model.KunnStatus.PENDING_APPROVAL AND k.limitId = :limitId AND (:excludeKunnId IS NULL OR k.id <> :excludeKunnId)")
    java.math.BigDecimal sumPendingLnAmtByLimitId(@Param("limitId") UUID limitId, @Param("excludeKunnId") UUID excludeKunnId);

    @Query("SELECT COALESCE(SUM(k.lnAmt), 0) FROM KunnEntity k WHERE k.status = vix.local.api.modules.capital_source.domain.model.KunnStatus.PENDING_APPROVAL AND k.limitId IN (SELECT c.id FROM CreditLimitEntity c WHERE c.contractId = :contractId) AND (:excludeKunnId IS NULL OR k.id <> :excludeKunnId)")
    java.math.BigDecimal sumPendingLnAmtByContractId(@Param("contractId") UUID contractId, @Param("excludeKunnId") UUID excludeKunnId);

    @Query("SELECT COALESCE(SUM(k.lnAmt), 0) FROM KunnEntity k WHERE k.status = vix.local.api.modules.capital_source.domain.model.KunnStatus.PENDING_APPROVAL AND k.cusId = :partnerId AND (:excludeKunnId IS NULL OR k.id <> :excludeKunnId)")
    java.math.BigDecimal sumPendingLnAmtByPartnerId(@Param("partnerId") UUID partnerId, @Param("excludeKunnId") UUID excludeKunnId);
}
