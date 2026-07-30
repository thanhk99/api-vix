package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Authorization;
import java.util.List;
import java.util.UUID;

public interface AuthorizationRepository {
    Authorization save(Authorization authorization);
    List<Authorization> findByPartnerId(UUID partnerId);
    void deleteById(UUID id);
    Authorization findById(UUID id);
}