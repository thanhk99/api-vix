package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetPledgeReleaseEntity;

import java.util.List;

@Repository
public interface AssetPledgeReleaseJpaRepository extends JpaRepository<AssetPledgeReleaseEntity, Long> {
    List<AssetPledgeReleaseEntity> findByPledgeId(Long pledgeId);
    Page<AssetPledgeReleaseEntity> findByPledgeId(Long pledgeId, Pageable pageable);
}
