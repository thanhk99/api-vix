package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.model.PartnerBankAccount;
import vix.local.api.modules.capital_source.domain.repository.PartnerBankAccountRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PartnerBankAccountApplicationService {

    private final PartnerBankAccountRepository bankAccountRepository;
    private final PartnerRepository partnerRepository;

    public PartnerBankAccount createBankAccount(UUID partnerId, PartnerBankAccount account, UUID creatorId) {
        vix.local.api.modules.capital_source.domain.model.Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new PartnerException("Không tìm thấy đối tác");
        }
        account.setPartnerId(partnerId);
        if (vix.local.api.modules.capital_source.domain.model.Partner.STATUS_DRAFT.equals(partner.getStatus()) || 
            vix.local.api.modules.capital_source.domain.model.Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            account.setStatus(PartnerBankAccount.STATUS_PENDING_APPROVAL);
        } else {
            account.setStatus(PartnerBankAccount.STATUS_ACTIVE);
        }
        account.setCreatedBy(creatorId);
        return bankAccountRepository.save(account);
    }

    public PartnerBankAccount updateBankAccount(UUID partnerId, UUID accountId, PartnerBankAccount updateRequest, UUID updaterId) {
        PartnerBankAccount account = bankAccountRepository.findById(accountId);
        if (account == null || !account.getPartnerId().equals(partnerId) || PartnerBankAccount.STATUS_DELETED.equals(account.getStatus())) {
            throw new PartnerException("Không tìm thấy tài khoản ngân hàng");
        }
        
        account.setAccountNumber(updateRequest.getAccountNumber());
        account.setAccountName(updateRequest.getAccountName());
        account.setBranch(updateRequest.getBranch());
        account.setPurpose(updateRequest.getPurpose());
        account.setAccountType(updateRequest.getAccountType());
        account.setTradingGateway(updateRequest.getTradingGateway());
        account.setOpenPlace(updateRequest.getOpenPlace());
        
        // Status can only be changed between ACTIVE and INACTIVE by user
        if (updateRequest.getStatus() != null && 
            (PartnerBankAccount.STATUS_ACTIVE.equals(updateRequest.getStatus()) || 
             "INACTIVE".equals(updateRequest.getStatus()))) {
            account.setStatus(updateRequest.getStatus());
        }
        
        account.setUpdatedBy(updaterId);
        
        return bankAccountRepository.save(account);
    }

    public void deleteBankAccount(UUID partnerId, UUID accountId, UUID updaterId) {
        PartnerBankAccount account = bankAccountRepository.findById(accountId);
        if (account == null || !account.getPartnerId().equals(partnerId)) {
            throw new PartnerException("Không tìm thấy tài khoản ngân hàng");
        }
        account.markAsDeleted();
        account.setUpdatedBy(updaterId);
        bankAccountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Page<PartnerBankAccount> getBankAccountsByPartnerId(UUID partnerId, String accountType, Pageable pageable) {
        if (accountType != null && !accountType.isEmpty()) {
            return bankAccountRepository.findByPartnerIdAndAccountTypeAndStatusNot(partnerId, accountType, PartnerBankAccount.STATUS_DELETED, pageable);
        }
        return bankAccountRepository.findByPartnerIdAndStatusNot(partnerId, PartnerBankAccount.STATUS_DELETED, pageable);
    }
}
