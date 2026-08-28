package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.exception.PartnerSecuritiesAccountException;
import vix.local.api.modules.capital_source.domain.model.PartnerSecuritiesAccount;
import vix.local.api.modules.capital_source.domain.repository.PartnerSecuritiesAccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerSecuritiesAccountApplicationService {
    private final PartnerSecuritiesAccountRepository accountRepository;

    @Transactional
    public PartnerSecuritiesAccount createAccount(UUID partnerId, PartnerSecuritiesAccount account) {
        account.setPartnerId(partnerId);
        account.validate();
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Page<PartnerSecuritiesAccount> getAccountsByPartnerId(UUID partnerId, Pageable pageable) {
        return accountRepository.findByPartnerId(partnerId, pageable);
    }

    @Transactional
    public PartnerSecuritiesAccount updateAccount(UUID partnerId, UUID accountId, PartnerSecuritiesAccount updateRequest) {
        PartnerSecuritiesAccount existing = accountRepository.findById(accountId)
                .orElseThrow(() -> new PartnerSecuritiesAccountException("Không tìm thấy tài khoản chứng khoán"));
                
        if (!existing.getPartnerId().equals(partnerId)) {
            throw new PartnerSecuritiesAccountException("Tài khoản không thuộc về đối tác này");
        }
        
        existing.setAccountNumber(updateRequest.getAccountNumber());
        existing.setAccountName(updateRequest.getAccountName());
        existing.setTradingGateways(updateRequest.getTradingGateways());
        
        existing.validate();
        
        return accountRepository.save(existing);
    }

    @Transactional
    public void deleteAccount(UUID partnerId, UUID accountId) {
        PartnerSecuritiesAccount existing = accountRepository.findById(accountId)
                .orElseThrow(() -> new PartnerSecuritiesAccountException("Không tìm thấy tài khoản chứng khoán"));
                
        if (!existing.getPartnerId().equals(partnerId)) {
            throw new PartnerSecuritiesAccountException("Tài khoản không thuộc về đối tác này");
        }
        
        existing.markAsDeleted();
        accountRepository.save(existing);
    }
}