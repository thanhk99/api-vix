package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerSecuritiesAccountEntity;

import java.util.UUID;

@Repository
public interface PartnerSecuritiesAccountJpaRepository extends JpaRepository<PartnerSecuritiesAccountEntity, UUID> {
    Page<PartnerSecuritiesAccountEntity> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable);
}