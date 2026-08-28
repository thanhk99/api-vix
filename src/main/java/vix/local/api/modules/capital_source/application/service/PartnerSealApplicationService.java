package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.exception.PartnerSealException;
import vix.local.api.modules.capital_source.domain.repository.PartnerSealRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import vix.local.api.modules.capital_source.domain.model.PartnerSeal;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerSealApplicationService {
    private final PartnerSealRepository partnerSealRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerSeal createSeal(UUID partnerId, PartnerSeal seal, UUID updaterId) {
        vix.local.api.modules.capital_source.domain.model.Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new PartnerException("Không tìm thấy đối tác");
        }
        seal.setPartnerId(partnerId);
        seal.setUpdatedBy(updaterId);
        seal.validateSeal();
        seal.updateStatus();
        if (vix.local.api.modules.capital_source.domain.model.Partner.STATUS_DRAFT.equals(partner.getStatus()) || 
            vix.local.api.modules.capital_source.domain.model.Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            seal.setStatus("PENDING_APPROVAL");
        }
        return partnerSealRepository.save(seal);
    }

    @Transactional(readOnly = true)
    public Page<PartnerSeal> getSealsByPartnerId(UUID partnerId, Pageable pageable) {
        return partnerSealRepository.findByPartnerId(partnerId, pageable);
    }

    @Transactional
    public PartnerSeal updateSeal(UUID partnerId, UUID sealId, PartnerSeal updateRequest, UUID updaterId) {
        PartnerSeal existing = partnerSealRepository.findById(sealId)
                .orElseThrow(() -> new PartnerSealException("Không tìm thấy mẫu dấu"));
                
        if (!existing.getPartnerId().equals(partnerId)) {
            throw new PartnerSealException("Mẫu dấu không thuộc về đối tác này");
        }
        
        existing.setSealFileName(updateRequest.getSealFileName());
        existing.setEffectiveDate(updateRequest.getEffectiveDate());
        existing.setExpiryDate(updateRequest.getExpiryDate());
        existing.setUpdatedBy(updaterId);
        
        existing.validateSeal();
        existing.updateStatus();
        
        return partnerSealRepository.save(existing);
    }

    @Transactional
    public void deleteSeal(UUID partnerId, UUID sealId) {
        PartnerSeal existing = partnerSealRepository.findById(sealId)
                .orElseThrow(() -> new PartnerSealException("Không tìm thấy mẫu dấu"));
                
        if (!existing.getPartnerId().equals(partnerId)) {
            throw new PartnerSealException("Mẫu dấu không thuộc về đối tác này");
        }
        
        existing.markAsDeleted();
        partnerSealRepository.save(existing);
    }
}