package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Authorization;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorizationRepository {
    Authorization save(Authorization authorization);
    Page<Authorization> findByPartnerId(UUID partnerId, Pageable pageable);
    void deleteById(UUID id);
    Authorization findById(UUID id);
    Integer getMaxSeqIdByPartnerId(UUID partnerId);
}