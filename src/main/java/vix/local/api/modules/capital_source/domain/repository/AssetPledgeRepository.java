package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.AssetPledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AssetPledgeRepository {
    AssetPledge save(AssetPledge pledge);
    Optional<AssetPledge> findById(Long id);
    Page<AssetPledge> findByFilters(String cusId, String contractNo, String limitId, String assetId, String status, Pageable pageable);
    boolean existsByAssetId(String assetId);
}
