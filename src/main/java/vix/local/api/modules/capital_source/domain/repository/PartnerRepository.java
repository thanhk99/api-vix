package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Partner;
import java.util.List;
import java.util.UUID;

public interface PartnerRepository {
    Partner save(Partner partner);
    Partner findById(UUID id);
    List<Partner> findAll();
    void deleteById(UUID id);
}