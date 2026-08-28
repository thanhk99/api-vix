package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.model.PartnerContact;
import vix.local.api.modules.capital_source.domain.repository.PartnerContactRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PartnerContactApplicationService {

    private final PartnerContactRepository contactRepository;
    private final PartnerRepository partnerRepository;

    public PartnerContact createContact(UUID partnerId, PartnerContact contact, UUID creatorId) {
        vix.local.api.modules.capital_source.domain.model.Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new PartnerException("Không tìm thấy đối tác");
        }
        contact.setPartnerId(partnerId);
        if (contact.getStatus() == null || contact.getStatus().trim().isEmpty()) {
            if (vix.local.api.modules.capital_source.domain.model.Partner.STATUS_DRAFT.equals(partner.getStatus()) || 
                vix.local.api.modules.capital_source.domain.model.Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
                contact.setStatus(PartnerContact.STATUS_PENDING_APPROVAL);
            } else {
                contact.setStatus(PartnerContact.STATUS_ACTIVE);
            }
        }
        contact.setCreatedBy(creatorId);
        return contactRepository.save(contact);
    }

    public PartnerContact updateContact(UUID partnerId, UUID contactId, PartnerContact updateRequest, UUID updaterId) {
        PartnerContact contact = contactRepository.findById(contactId);
        if (contact == null || !contact.getPartnerId().equals(partnerId) || PartnerContact.STATUS_DELETED.equals(contact.getStatus())) {
            throw new PartnerException("Không tìm thấy thông tin liên hệ");
        }
        
        contact.setName(updateRequest.getName());
        contact.setPosition(updateRequest.getPosition());
        contact.setDepartment(updateRequest.getDepartment());
        contact.setPhone(updateRequest.getPhone());
        contact.setEmail(updateRequest.getEmail());
        contact.setRole(updateRequest.getRole());
        contact.setTransactionFee(updateRequest.getTransactionFee());
        contact.setNote(updateRequest.getNote());
        if (updateRequest.getStatus() != null && !updateRequest.getStatus().trim().isEmpty()) {
            contact.setStatus(updateRequest.getStatus());
        }
        contact.setUpdatedBy(updaterId);
        
        return contactRepository.save(contact);
    }

    public void deleteContact(UUID partnerId, UUID contactId, UUID updaterId) {
        PartnerContact contact = contactRepository.findById(contactId);
        if (contact == null || !contact.getPartnerId().equals(partnerId)) {
            throw new PartnerException("Không tìm thấy thông tin liên hệ");
        }
        contact.markAsDeleted();
        contact.setUpdatedBy(updaterId);
        contactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    public Page<PartnerContact> getContactsByPartnerId(UUID partnerId, Pageable pageable) {
        return contactRepository.findByPartnerIdAndStatusNot(partnerId, PartnerContact.STATUS_DELETED, pageable);
    }
}
