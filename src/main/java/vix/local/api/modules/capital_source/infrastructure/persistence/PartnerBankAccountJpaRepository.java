package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerBankAccountEntity;
import java.util.UUID;

@Repository
public interface PartnerBankAccountJpaRepository extends JpaRepository<PartnerBankAccountEntity, UUID> {
    Page<PartnerBankAccountEntity> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable);
    Page<PartnerBankAccountEntity> findByPartnerIdAndAccountTypeAndStatusNot(UUID partnerId, String accountType, String status, Pageable pageable);
}
