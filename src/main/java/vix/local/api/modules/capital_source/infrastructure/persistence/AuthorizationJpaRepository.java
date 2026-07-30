package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AuthorizationEntity;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorizationJpaRepository extends JpaRepository<AuthorizationEntity, UUID> {
    @Query("SELECT a FROM AuthorizationEntity a WHERE a.partnerId = :partnerId")
    List<AuthorizationEntity> findByPartnerId(@Param("partnerId") UUID partnerId);
}