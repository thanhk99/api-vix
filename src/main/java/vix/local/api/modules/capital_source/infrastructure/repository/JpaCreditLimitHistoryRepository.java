package vix.local.api.modules.capital_source.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitHistoryEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface JpaCreditLimitHistoryRepository extends JpaRepository<CreditLimitHistoryEntity, UUID> {
    Page<CreditLimitHistoryEntity> findByCreditLimitIdOrderByTransactionDateDesc(UUID creditLimitId, Pageable pageable);

    @Query("SELECT h FROM CreditLimitHistoryEntity h WHERE " +
           "(:creditLimitId IS NULL OR h.creditLimitId = :creditLimitId) AND " +
           "(:fromDate IS NULL OR h.transactionDate >= :fromDate) AND " +
           "(:toDate IS NULL OR h.transactionDate <= :toDate) " +
           "ORDER BY h.transactionDate DESC")
    Page<CreditLimitHistoryEntity> findByFilters(
            @Param("creditLimitId") UUID creditLimitId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
