package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.PartnerSecuritiesAccount;

import java.util.Optional;
import java.util.UUID;

public interface PartnerSecuritiesAccountRepository {
    PartnerSecuritiesAccount save(PartnerSecuritiesAccount account);
    Optional<PartnerSecuritiesAccount> findById(UUID id);
    Page<PartnerSecuritiesAccount> findByPartnerId(UUID partnerId, Pageable pageable);
    void deleteById(UUID id);
}