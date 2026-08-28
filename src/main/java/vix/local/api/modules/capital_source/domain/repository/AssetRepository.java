package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    
    Optional<Asset> findByAssetId(String assetId);
    
    boolean existsByAssetId(String assetId);
    
    Page<Asset> findByFilters(String assetId, String assetType, String symbol, String status, Pageable pageable);
    
    void deleteById(UUID id);
    
    Optional<Asset> findById(UUID id);
    
    long count();
}