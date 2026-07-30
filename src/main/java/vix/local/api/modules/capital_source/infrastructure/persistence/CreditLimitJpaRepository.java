package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitEntity;
import java.util.List;
import java.util.UUID;

@Repository
public interface CreditLimitJpaRepository extends JpaRepository<CreditLimitEntity, UUID> {
    @Query("SELECT c FROM CreditLimitEntity c WHERE c.partnerId = :partnerId")
    List<CreditLimitEntity> findByPartnerId(@Param("partnerId") UUID partnerId);
}