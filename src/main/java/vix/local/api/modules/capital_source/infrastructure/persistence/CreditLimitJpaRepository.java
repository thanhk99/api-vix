package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitEntity;
import java.util.UUID;

@Repository
public interface CreditLimitJpaRepository extends JpaRepository<CreditLimitEntity, UUID> {
       @Query("SELECT c FROM CreditLimitEntity c WHERE c.partnerId = :partnerId AND c.status != 'DELETED' ORDER BY c.updatedAt DESC")
       Page<CreditLimitEntity> findByPartnerId(@Param("partnerId") UUID partnerId, Pageable pageable);

       @Query("SELECT c FROM CreditLimitEntity c WHERE c.status != 'DELETED' AND c.parentId IS NULL " +
                     "AND (:partnerId IS NULL OR c.partnerId = :partnerId) " +
                     "AND (:limitId IS NULL OR c.limitId LIKE CONCAT('%', CAST(:limitId AS String), '%')) " +
                     "AND (:contactNo IS NULL OR c.contactNo LIKE CONCAT('%', CAST(:contactNo AS String), '%')) " +
                     "AND (:poolType IS NULL OR c.poolType = :poolType) " +
                     "AND (:status IS NULL OR c.status = :status) " +
                     "AND (CAST(:startDate AS date) IS NULL OR c.startDate >= :startDate) " +
                     "AND (CAST(:endDate AS date) IS NULL OR c.endDate <= :endDate) " +
                     "ORDER BY c.updatedAt DESC")
       Page<CreditLimitEntity> searchGlobal(
                     @Param("partnerId") UUID partnerId,
                     @Param("limitId") String limitId,
                     @Param("contactNo") String contactNo,
                     @Param("poolType") String poolType,
                     @Param("status") String status,
                     @Param("startDate") java.time.LocalDate startDate,
                     @Param("endDate") java.time.LocalDate endDate,
                     Pageable pageable);

       java.util.List<CreditLimitEntity> findByParentIdInAndStatusNot(java.util.List<UUID> parentIds, String status);

       java.util.List<CreditLimitEntity> findByPartnerIdAndStatus(UUID partnerId, String status);
}