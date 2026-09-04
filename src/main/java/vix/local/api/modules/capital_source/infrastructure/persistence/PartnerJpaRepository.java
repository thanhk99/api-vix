package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerEntity;
import java.util.UUID;

@Repository
public interface PartnerJpaRepository extends JpaRepository<PartnerEntity, UUID> {
    
    @Query("SELECT p FROM PartnerEntity p WHERE (p.status NOT IN ('DELETED', 'DRAFT') OR p.status IS NULL) AND (p.cusId NOT LIKE 'DRAFT_%' AND p.cusName != 'Bản nháp') ORDER BY p.lastUpdated DESC")
    Page<PartnerEntity> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM PartnerEntity p WHERE LOWER(TRIM(p.cusId)) = LOWER(TRIM(:cusId)) AND (p.status != 'DELETED' OR p.status IS NULL)")
    boolean existsByCusIdIgnoreCase(@Param("cusId") String cusId);

    @Query("SELECT COUNT(p) > 0 FROM PartnerEntity p WHERE LOWER(TRIM(p.cusId)) = LOWER(TRIM(:cusId)) AND p.id != :id AND (p.status != 'DELETED' OR p.status IS NULL)")
    boolean existsByCusIdIgnoreCaseAndIdNot(@Param("cusId") String cusId, @Param("id") UUID id);

    @Query("SELECT COUNT(p) > 0 FROM PartnerEntity p WHERE LOWER(TRIM(p.branchCusId)) = LOWER(TRIM(:branchCusId)) AND (p.status != 'DELETED' OR p.status IS NULL)")
    boolean existsByBranchCusIdIgnoreCase(@Param("branchCusId") String branchCusId);

    @Query("SELECT COUNT(p) > 0 FROM PartnerEntity p WHERE LOWER(TRIM(p.branchCusId)) = LOWER(TRIM(:branchCusId)) AND p.id != :id AND (p.status != 'DELETED' OR p.status IS NULL)")
    boolean existsByBranchCusIdIgnoreCaseAndIdNot(@Param("branchCusId") String branchCusId, @Param("id") UUID id);
}