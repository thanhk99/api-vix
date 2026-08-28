package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetEntity;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetJpaRepository extends JpaRepository<AssetEntity, UUID> {
    Optional<AssetEntity> findByAssetId(String assetId);
    
    boolean existsByAssetId(String assetId);
    
    @Query("SELECT a FROM AssetEntity a WHERE " +
           "(:assetId IS NULL OR a.assetId LIKE %:assetId%) AND " +
           "(:assetType IS NULL OR a.assetType = :assetType) AND " +
           "(:symbol IS NULL OR a.symbol LIKE %:symbol%) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<AssetEntity> findByFilters(
        @Param("assetId") String assetId, 
        @Param("assetType") String assetType, 
        @Param("symbol") String symbol, 
        @Param("status") String status, 
        Pageable pageable
    );
}