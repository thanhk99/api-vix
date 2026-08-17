package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.AuthorizationEntity;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface AuthorizationJpaRepository extends JpaRepository<AuthorizationEntity, UUID> {
    
    Page<AuthorizationEntity> findByPartnerId(UUID partnerId, Pageable pageable);
    
    Optional<AuthorizationEntity> findTopByPartnerIdOrderBySeqIdDesc(UUID partnerId);
}