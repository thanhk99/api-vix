package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetTransactionEntity;

@Repository
public interface AssetTransactionJpaRepository extends JpaRepository<AssetTransactionEntity, Long> {

    @Query("SELECT t FROM AssetTransactionEntity t WHERE " +
           "(:transType IS NULL OR t.transType = :transType) AND " +
           "(:counterpartyId IS NULL OR t.counterpartyId = :counterpartyId) AND " +
           "(:assetId IS NULL OR t.assetId = :assetId) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<AssetTransactionEntity> findByFilters(
            @Param("transType") String transType,
            @Param("counterpartyId") String counterpartyId,
            @Param("assetId") String assetId,
            @Param("status") String status,
            Pageable pageable
    );

    boolean existsByAssetId(String assetId);
}
