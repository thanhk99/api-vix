package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerEntity;
import java.util.UUID;

@Repository
public interface PartnerJpaRepository extends JpaRepository<PartnerEntity, UUID> {
}