package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.PartnerAsset;
import java.util.List;
import java.util.UUID;

public interface PartnerAssetRepository {
    PartnerAsset save(PartnerAsset asset);
    PartnerAsset findById(UUID id);
    List<PartnerAsset> findByPoolId(UUID poolId);
}