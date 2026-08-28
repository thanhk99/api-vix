package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditContractEntity;

import java.util.UUID;

@Repository
public interface CreditContractJpaRepository extends JpaRepository<CreditContractEntity, UUID> {
    Page<CreditContractEntity> findByPartnerId(UUID partnerId, Pageable pageable);
    java.util.List<CreditContractEntity> findByPartnerIdAndStatus(UUID partnerId, String status);
}