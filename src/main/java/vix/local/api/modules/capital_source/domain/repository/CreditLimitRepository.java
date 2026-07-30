package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import java.util.List;
import java.util.UUID;

public interface CreditLimitRepository {
    CreditLimit save(CreditLimit creditLimit);
    List<CreditLimit> findByPartnerId(UUID partnerId);
    void deleteById(UUID id);
    CreditLimit findById(UUID id);
}