package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;
import vix.local.api.modules.capital_source.domain.model.CreditContract;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.CreditContractRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;

import java.util.UUID;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditContractApplicationService {

    private final CreditContractRepository contractRepository;
    private final PartnerRepository partnerRepository;

    public CreditContract createContract(UUID partnerId, CreditContract contract) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new CreditLimitException("Không tìm thấy đối tác");
        }

        contract.setPartnerId(partnerId);
        if (contract.getTotalLimit() != null) {
            contract.setRemainLimit(contract.getTotalLimit());
            contract.setUsedLimit(java.math.BigDecimal.ZERO);
        }
        contract.setStatus(CreditContract.STATUS_PENDING_APPROVAL);
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    public CreditContract updateContract(UUID contractId, CreditContract updateRequest) {
        CreditContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new CreditLimitException("Không tìm thấy hợp đồng");
        }
        if (CreditContract.STATUS_DELETED.equals(contract.getStatus())) {
            throw new CreditLimitException("Không thể cập nhật hợp đồng đã bị xoá");
        }

        contract.setContractNo(updateRequest.getContractNo());
        contract.setPurpose(updateRequest.getPurpose());
        contract.setStartDate(updateRequest.getStartDate());
        contract.setEndDate(updateRequest.getEndDate());
        
        if (updateRequest.getTotalLimit() != null) {
            contract.setTotalLimit(updateRequest.getTotalLimit());
            contract.initRemain();
        }
        
        contract.setStatus(CreditContract.STATUS_PENDING_APPROVAL);
        contract.setUpdatedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    public CreditContract approveContract(UUID contractId, UUID approverId) {
        CreditContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new CreditLimitException("Không tìm thấy hợp đồng");
        }
        if (CreditContract.STATUS_DELETED.equals(contract.getStatus())) {
            throw new CreditLimitException("Không thể duyệt hợp đồng đã bị xoá");
        }

        contract.setStatus(CreditContract.STATUS_APPROVED);
        contract.setApprovedBy(approverId);
        contract.setApprovedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    public void deleteContract(UUID contractId) {
        CreditContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new CreditLimitException("Không tìm thấy hợp đồng");
        }
        contract.markAsPendingDelete();
        contractRepository.save(contract);
    }

    public org.springframework.data.domain.Page<CreditContract> getContractsByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return contractRepository.findByPartnerId(partnerId, pageable);
    }
}
