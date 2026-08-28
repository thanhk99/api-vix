package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.AssetPledgeRelease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface AssetPledgeReleaseRepository {
    AssetPledgeRelease save(AssetPledgeRelease release);
    Optional<AssetPledgeRelease> findById(Long id);
    List<AssetPledgeRelease> findByPledgeId(Long pledgeId);
    Page<AssetPledgeRelease> findByPledgeId(Long pledgeId, Pageable pageable);
    Page<AssetPledgeRelease> findAll(Pageable pageable);
}
