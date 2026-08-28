package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.PartnerBankAccount;
import java.util.UUID;

public interface PartnerBankAccountRepository {
    PartnerBankAccount save(PartnerBankAccount account);
    PartnerBankAccount findById(UUID id);
    Page<PartnerBankAccount> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable);
    Page<PartnerBankAccount> findByPartnerIdAndAccountTypeAndStatusNot(UUID partnerId, String accountType, String status, Pageable pageable);
    void deleteById(UUID id);
}
