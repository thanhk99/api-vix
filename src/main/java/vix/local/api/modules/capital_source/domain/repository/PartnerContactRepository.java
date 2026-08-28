package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.PartnerContact;
import java.util.UUID;

public interface PartnerContactRepository {
    PartnerContact save(PartnerContact contact);
    PartnerContact findById(UUID id);
    Page<PartnerContact> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable);
    void deleteById(UUID id);
}
