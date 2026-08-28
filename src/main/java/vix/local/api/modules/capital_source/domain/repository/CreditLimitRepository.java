package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CreditLimitRepository {
    CreditLimit save(CreditLimit creditLimit);
    Page<CreditLimit> findByPartnerId(UUID partnerId, Pageable pageable);
    Page<CreditLimit> searchGlobal(
            UUID partnerId, 
            String limitId, 

            String poolType, 
            String status, 
            java.time.LocalDate startDate, 
            java.time.LocalDate endDate, 
            Pageable pageable);
    
    Page<CreditLimit> findByContractId(UUID contractId, Pageable pageable);
    java.util.List<CreditLimit> findByContractIdIn(java.util.List<UUID> contractIds);
    void deleteById(UUID id);
    CreditLimit findById(UUID id);
    
    java.util.List<CreditLimit> saveAll(java.util.List<CreditLimit> creditLimits);
    java.util.List<CreditLimit> findByPartnerIdAndStatus(UUID partnerId, String status);
    java.util.List<CreditLimit> findAll();
    CreditLimit findByLimitId(String limitId);
}
