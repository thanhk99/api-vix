package vix.local.api.modules.capital_source.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitHistoryEntity;

import java.util.UUID;

@Repository
public interface JpaCreditLimitHistoryRepository extends JpaRepository<CreditLimitHistoryEntity, UUID>, JpaSpecificationExecutor<CreditLimitHistoryEntity> {
    Page<CreditLimitHistoryEntity> findByCreditLimitIdOrderByTransactionDateDesc(UUID creditLimitId, Pageable pageable);
}
