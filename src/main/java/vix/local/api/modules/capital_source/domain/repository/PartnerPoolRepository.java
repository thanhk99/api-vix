package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.PartnerPool;
import java.util.List;
import java.util.UUID;

public interface PartnerPoolRepository {
    PartnerPool save(PartnerPool pool);
    PartnerPool findById(UUID id);
    List<PartnerPool> findByPartnerId(UUID partnerId);
}