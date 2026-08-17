package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PartnerRepository {
    Partner save(Partner partner);
    Partner findById(UUID id);
    Page<Partner> findAll(Pageable pageable);
    void deleteById(UUID id);
}