package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.PartnerSignature;
import java.util.UUID;

public interface PartnerSignatureRepository {
    PartnerSignature save(PartnerSignature signature);
    PartnerSignature findById(UUID id);
    Page<PartnerSignature> findByPartnerId(UUID partnerId, Pageable pageable);
    void deleteById(UUID id);
}
