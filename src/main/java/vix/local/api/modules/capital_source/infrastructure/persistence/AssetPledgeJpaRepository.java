package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetPledgeEntity;

@Repository
public interface AssetPledgeJpaRepository extends JpaRepository<AssetPledgeEntity, Long> {

    @Query("SELECT p FROM AssetPledgeEntity p WHERE " +
           "(:cusId IS NULL OR p.cusId = :cusId) AND " +
           "(:contractNo IS NULL OR p.contractNo = :contractNo) AND " +
           "(:limitId IS NULL OR p.limitId = :limitId) AND " +
           "(:assetId IS NULL OR p.assetId = :assetId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<AssetPledgeEntity> findByFilters(
            @Param("cusId") String cusId,
            @Param("contractNo") String contractNo,
            @Param("limitId") String limitId,
            @Param("assetId") String assetId,
            @Param("status") String status,
            Pageable pageable
    );

    boolean existsByAssetId(String assetId);
}
