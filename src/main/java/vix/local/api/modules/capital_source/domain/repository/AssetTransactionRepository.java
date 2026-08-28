package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.AssetTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AssetTransactionRepository {
    AssetTransaction save(AssetTransaction transaction);
    Optional<AssetTransaction> findById(Long id);
    Page<AssetTransaction> findByFilters(String transType, String counterpartyId, String assetId, String status, Pageable pageable);
    boolean existsByAssetId(String assetId);
}
