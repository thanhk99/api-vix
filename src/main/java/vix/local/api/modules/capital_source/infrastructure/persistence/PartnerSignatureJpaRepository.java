package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerSignatureEntity;

import java.util.UUID;

@Repository
public interface PartnerSignatureJpaRepository extends JpaRepository<PartnerSignatureEntity, UUID> {
    Page<PartnerSignatureEntity> findByPartnerId(UUID partnerId, Pageable pageable);
    Page<PartnerSignatureEntity> findByPartnerIdOrderByUpdatedAtDesc(UUID partnerId, Pageable pageable);
}
