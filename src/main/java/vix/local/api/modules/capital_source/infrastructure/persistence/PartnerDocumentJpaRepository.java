package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerDocumentEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerDocumentJpaRepository extends JpaRepository<PartnerDocumentEntity, UUID> {
    List<PartnerDocumentEntity> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId);
}
