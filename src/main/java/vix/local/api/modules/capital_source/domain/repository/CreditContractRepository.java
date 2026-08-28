package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.CreditContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface CreditContractRepository {
    CreditContract save(CreditContract contract);
    CreditContract findById(UUID id);
    Page<CreditContract> findByPartnerId(UUID partnerId, Pageable pageable);
    List<CreditContract> findByPartnerIdAndStatus(UUID partnerId, String status);
    void deleteById(UUID id);
}
