package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Asset;
import java.util.List;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    List<Asset> findByPartnerId(UUID partnerId);
    List<Asset> findByCreditLimitId(UUID creditLimitId);
    void deleteById(UUID id);
    Asset findById(UUID id);
}