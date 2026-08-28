package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.PartnerSeal;

import java.util.Optional;
import java.util.UUID;

public interface PartnerSealRepository {
    PartnerSeal save(PartnerSeal partnerSeal);
    Optional<PartnerSeal> findById(UUID id);
    Page<PartnerSeal> findByPartnerId(UUID partnerId, Pageable pageable);
    void deleteById(UUID id);
}