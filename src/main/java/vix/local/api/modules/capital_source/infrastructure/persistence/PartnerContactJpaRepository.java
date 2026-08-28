package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerContactEntity;
import java.util.UUID;

@Repository
public interface PartnerContactJpaRepository extends JpaRepository<PartnerContactEntity, UUID> {
    Page<PartnerContactEntity> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable);
}
