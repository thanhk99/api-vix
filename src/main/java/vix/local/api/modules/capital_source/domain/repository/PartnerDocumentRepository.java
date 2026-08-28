package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.PartnerDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerDocumentRepository {
    List<PartnerDocument> findByPartnerId(UUID partnerId);
    Optional<PartnerDocument> findById(UUID id);
    PartnerDocument save(PartnerDocument document);
    void deleteById(UUID id);
}
