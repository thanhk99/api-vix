package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerEntity;
import java.util.UUID;

@Repository
public interface PartnerJpaRepository extends JpaRepository<PartnerEntity, UUID> {
    
    @Query("SELECT p FROM PartnerEntity p WHERE (p.status NOT IN ('DELETED', 'DRAFT') OR p.status IS NULL) AND (p.cusId NOT LIKE 'DRAFT_%' AND p.cusName != 'Bản nháp') ORDER BY p.lastUpdated DESC")
    Page<PartnerEntity> findAllActive(Pageable pageable);
}