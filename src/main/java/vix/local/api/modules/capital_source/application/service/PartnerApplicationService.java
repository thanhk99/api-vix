package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.model.Kunn;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.repository.AuthorizationRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import vix.local.api.modules.capital_source.domain.repository.KunnRepository;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class PartnerApplicationService {

    private final PartnerRepository partnerRepository;
    private final AuthorizationRepository authorizationRepository;
    private final CreditLimitRepository creditLimitRepository;
    private final CreditLimitHistoryRepository creditLimitHistoryRepository;
    private final AssetRepository assetRepository;
    private final KunnRepository kunnRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerSignatureRepository partnerSignatureRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerSealRepository partnerSealRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerBankAccountRepository partnerBankAccountRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerContactRepository partnerContactRepository;
    private final vix.local.api.modules.document.application.port.DocumentPort documentPort;
    
    private final vix.local.api.modules.capital_source.domain.repository.CreditContractRepository contractRepository;
    private final vix.local.api.modules.capital_source.domain.service.PartnerDomainService partnerDomainService;


    // QuÃÂ¡ÃÂºÃÂ£n lÃÆÃÂ½ chÃÂ¡ÃÂ»ÃÂ¯ kÃÆÃÂ½ cho ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c
    public vix.local.api.modules.capital_source.domain.model.PartnerSignature createSignature(UUID partnerId, vix.local.api.modules.capital_source.domain.model.PartnerSignature signature, UUID updaterId) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        }
        signature.setPartnerId(partnerId);
        signature.setUpdatedBy(updaterId);
        signature.validateSignature();
        signature.updateStatus();
        if (Partner.STATUS_DRAFT.equals(partner.getStatus()) || Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            signature.setStatus("PENDING_APPROVAL");
        }
        return partnerSignatureRepository.save(signature);
    }

    public vix.local.api.modules.capital_source.domain.model.PartnerSignature updateSignature(UUID partnerId, UUID signatureId, vix.local.api.modules.capital_source.domain.model.PartnerSignature updateRequest, UUID updaterId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || !signature.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException("Không tìm th?y ch? ký");
        }
        
        signature.setSignFileName(updateRequest.getSignFileName());
        signature.setSignType(updateRequest.getSignType());
        signature.setDescription(updateRequest.getDescription());
        signature.setEffectiveDate(updateRequest.getEffectiveDate());
        signature.setExpiryDate(updateRequest.getExpiryDate());
        signature.setUpdatedBy(updaterId);
        
        signature.validateSignature();
        signature.updateStatus();
        
        return partnerSignatureRepository.save(signature);
    }

    public void deleteSignature(UUID partnerId, UUID signatureId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || !signature.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException("Không tìm th?y ch? ký");
        }
        partnerSignatureRepository.deleteById(signatureId);
    }

    public org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerSignature> getSignaturesByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return partnerSignatureRepository.findByPartnerId(partnerId, pageable);
    }

    public Partner createPartner(Partner partner) {
        // Domain validation (quy tắc nghiệp vụ đối tác + tính duy nhất Mã đơn vị GD)
        partnerDomainService.validateForCreation(partner);

        // Khởi tạo trạng thái và hạn mức ban đầu theo nghiệp vụ Domain
        partner.initializeForCreation();

        // Lưu đối tác mới
        return partnerRepository.save(partner);
    }
    
    public Partner updatePartner(UUID id, Partner updateRequest) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }

        // Domain validation: Kiểm tra tính duy nhất của Mã đơn vị GD (trừ đối tác hiện tại)
        partnerDomainService.validateBranchCusIdUniqueness(updateRequest.getBranchCusId(), id);
        
        // Lưu snapshot trạng thái gốc vào changeReason trước khi cập nhật
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            java.util.Map<String, Object> snapshot = new java.util.HashMap<>();
            snapshot.put("cusId", partner.getCusId());
            snapshot.put("branchCusId", partner.getBranchCusId());
            snapshot.put("cusName", partner.getCusName());
            snapshot.put("shortName", partner.getShortName());
            snapshot.put("idCode", partner.getIdCode());
            snapshot.put("fistIssueDate", partner.getFistIssueDate() != null ? partner.getFistIssueDate().toString() : null);
            snapshot.put("lastIssueDate", partner.getLastIssueDate() != null ? partner.getLastIssueDate().toString() : null);
            snapshot.put("issueBy", partner.getIssueBy());
            snapshot.put("opLiscenseNo", partner.getOpLiscenseNo());
            snapshot.put("opIssueDate", partner.getOpIssueDate() != null ? partner.getOpIssueDate().toString() : null);
            snapshot.put("address", partner.getAddress());
            snapshot.put("mobile", partner.getMobile());
            snapshot.put("email", partner.getEmail());
            snapshot.put("website", partner.getWebsite());
            snapshot.put("cusType", partner.getCusType());
            snapshot.put("businessType", partner.getBusinessType());
            snapshot.put("professionalInvestor", partner.getProfessionalInvestor());
            snapshot.put("depositoryMemberCode", partner.getDepositoryMemberCode());
            snapshot.put("tradingGateway", partner.getTradingGateway());
            snapshot.put("generalNote", partner.getGeneralNote());
            
            partner.setNote(mapper.writeValueAsString(snapshot));
        } catch (Exception ignored) {
        }
        
        partner.updatePartnerInfo(updateRequest.getCusId(), updateRequest.getCusName());
        
        // Cập nhật các trường khác
        partner.setBranchCusId(updateRequest.getBranchCusId());
        partner.setShortName(updateRequest.getShortName());
        partner.setAddress(updateRequest.getAddress());
        partner.setIdCode(updateRequest.getIdCode());
        partner.setFistIssueDate(updateRequest.getFistIssueDate());
        if (updateRequest.getChangeCount() != null) {
            partner.setChangeCount(updateRequest.getChangeCount());
        }
        if (partner.getChangeCount() == null || partner.getChangeCount() == 0) {
            partner.setChangeCount(0);
            partner.setLastIssueDate(updateRequest.getFistIssueDate() != null ? updateRequest.getFistIssueDate() : updateRequest.getLastIssueDate());
            partner.setChangeReason("");
        } else {
            partner.setLastIssueDate(updateRequest.getLastIssueDate() != null ? updateRequest.getLastIssueDate() : updateRequest.getFistIssueDate());
            partner.setChangeReason(updateRequest.getChangeReason() != null ? updateRequest.getChangeReason() : "");
        }
        partner.setIssueBy(updateRequest.getIssueBy());
        partner.setOpLiscenseNo(updateRequest.getOpLiscenseNo());
        partner.setOpIssueDate(updateRequest.getOpIssueDate());
        partner.setOpIssueBy(updateRequest.getOpIssueBy());
        partner.setMobile(updateRequest.getMobile());
        partner.setEmail(updateRequest.getEmail());
        partner.setWebsite(updateRequest.getWebsite());
        partner.setFax(updateRequest.getFax());
        partner.setGeneralNote(updateRequest.getGeneralNote());
        if (updateRequest.getCusType() != null) partner.setCusType(updateRequest.getCusType());
        if (updateRequest.getBusinessType() != null) partner.setBusinessType(updateRequest.getBusinessType());
        if (updateRequest.getProfessionalInvestor() != null) partner.setProfessionalInvestor(updateRequest.getProfessionalInvestor());
        partner.setDepositoryMemberCode(updateRequest.getDepositoryMemberCode());
        partner.setTradingGateway(updateRequest.getTradingGateway());
        if (updateRequest.getIsActive() != null) partner.setIsActive(updateRequest.getIsActive());
        
        partner.setStatus(Partner.STATUS_PENDING_APPROVAL);
        partner.setLastUpdated(LocalDate.now());
        
        if (updateRequest.getTotalPool() != null) {
            partner.setTotalPool(updateRequest.getTotalPool());
            if (partner.getUsedPool() == null) partner.setUsedPool(java.math.BigDecimal.ZERO);
            partner.setRemainPool(updateRequest.getTotalPool().subtract(partner.getUsedPool()));
        }
        
        partner.validatePartner();
        
        return partnerRepository.save(partner);
    }

    public java.util.Map<String, Object> checkDuplicate(String cusId, String branchCusId, UUID excludeId) {
        boolean branchCusIdDuplicate = partnerDomainService.isBranchCusIdDuplicate(branchCusId, excludeId);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("cusIdDuplicate", false);
        result.put("branchCusIdDuplicate", branchCusIdDuplicate);
        result.put("isDuplicate", branchCusIdDuplicate);
        return result;
    }
    
    public Partner updateCustomerType(UUID id, Partner updateRequest) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        }
        
        partner.updateCustomerTypeInfo(
            updateRequest.getCusType(), 
            updateRequest.getBusinessType(), 
            updateRequest.getProfessionalInvestor(), 
            updateRequest.getProfessionalStartDate(), 
            updateRequest.getProfessionalEndDate(), 
            updateRequest.getNote()
        );
        
        partner.validatePartner();
        
        return partnerRepository.save(partner);
    }
    
    public void deletePartner(UUID id) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        partner.markAsPendingDelete();
        partnerRepository.save(partner);
        
        org.springframework.data.domain.Page<CreditLimit> limits = creditLimitRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        for (CreditLimit limit : limits.getContent()) {
            if (!CreditLimit.STATUS_DELETED.equals(limit.getStatus())) {
                limit.markAsPendingDelete();
                creditLimitRepository.save(limit);
            }
        }
        
        List<Kunn> kunns = kunnRepository.findByCusId(id);
        kunns.forEach(Kunn::markAsPendingDelete);
        kunnRepository.saveAll(kunns);
    }
    
    public Partner approvePartner(UUID id, UUID approverId) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhA'ng tAm thy ``i tAc");
        }
        
        partner.markAsApproved(approverId);
        
        // Phê duyệt các bản ghi con (Signature, Seal, Authorization, BankAccount, Contact) nếu đang ở trạng thái PENDING_APPROVAL
        
        // 1. Signatures
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerSignature> sigPage = partnerSignatureRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        if (sigPage != null && sigPage.getContent() != null) {
            for (vix.local.api.modules.capital_source.domain.model.PartnerSignature sig : sigPage.getContent()) {
                if ("PENDING_APPROVAL".equals(sig.getStatus())) {
                    sig.updateStatus(); // returns it to APPROVED/DUEDATE based on logic
                    partnerSignatureRepository.save(sig);
                }
            }
        }
        
        // 2. Seals
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerSeal> sealPage = partnerSealRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        if (sealPage != null && sealPage.getContent() != null) {
            for (vix.local.api.modules.capital_source.domain.model.PartnerSeal seal : sealPage.getContent()) {
                if ("PENDING_APPROVAL".equals(seal.getStatus())) {
                    seal.updateStatus();
                    partnerSealRepository.save(seal);
                }
            }
        }
        
        // 3. Authorizations
        org.springframework.data.domain.Page<Authorization> authPage = authorizationRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        if (authPage != null && authPage.getContent() != null) {
            for (Authorization auth : authPage.getContent()) {
                if ("PENDING_APPROVAL".equals(auth.getStatus())) {
                    auth.updateStatus();
                    authorizationRepository.save(auth);
                }
            }
        }
        
        // 4. Bank Accounts
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerBankAccount> bankPage = partnerBankAccountRepository.findByPartnerIdAndStatusNot(id, vix.local.api.modules.capital_source.domain.model.PartnerBankAccount.STATUS_DELETED, org.springframework.data.domain.Pageable.unpaged());
        if (bankPage != null && bankPage.getContent() != null) {
            for (vix.local.api.modules.capital_source.domain.model.PartnerBankAccount acc : bankPage.getContent()) {
                if ("PENDING_APPROVAL".equals(acc.getStatus())) {
                    acc.setStatus(vix.local.api.modules.capital_source.domain.model.PartnerBankAccount.STATUS_ACTIVE);
                    partnerBankAccountRepository.save(acc);
                }
            }
        }
        
        // 5. Contacts
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerContact> contactPage = partnerContactRepository.findByPartnerIdAndStatusNot(id, vix.local.api.modules.capital_source.domain.model.PartnerContact.STATUS_DELETED, org.springframework.data.domain.Pageable.unpaged());
        if (contactPage != null && contactPage.getContent() != null) {
            for (vix.local.api.modules.capital_source.domain.model.PartnerContact contact : contactPage.getContent()) {
                if ("PENDING_APPROVAL".equals(contact.getStatus())) {
                    contact.setStatus(vix.local.api.modules.capital_source.domain.model.PartnerContact.STATUS_ACTIVE);
                    partnerContactRepository.save(contact);
                }
            }
        }
        
        partner.setNote(null);
        return partnerRepository.save(partner);
    }

    public Partner rejectPartner(UUID id, UUID rejecterId) {
        return rejectPartner(id, rejecterId, null);
    }

    @SuppressWarnings("unchecked")
    public Partner rejectPartner(UUID id, UUID rejecterId, java.util.Map<String, Object> body) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        
        java.util.Map<String, Object> snapshot = null;
        if (body != null && body.get("snapshot") instanceof java.util.Map) {
            snapshot = (java.util.Map<String, Object>) body.get("snapshot");
        } else if (partner.getNote() != null && partner.getNote().startsWith("{")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                snapshot = mapper.readValue(partner.getNote(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
            } catch (Exception ignored) {
            }
        } else if (partner.getChangeReason() != null && partner.getChangeReason().startsWith("{")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                snapshot = mapper.readValue(partner.getChangeReason(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
            } catch (Exception ignored) {
            }
        }
        
        // Nếu đối tác này là Chờ duyệt do SỬA (có snapshot hoặc đã từng có changeCount):
        if (snapshot != null) {
            if (snapshot.get("cusId") != null) partner.setCusId(String.valueOf(snapshot.get("cusId")));
            if (snapshot.get("branchCusId") != null) partner.setBranchCusId(String.valueOf(snapshot.get("branchCusId")));
            if (snapshot.get("cusName") != null) partner.setCusName(String.valueOf(snapshot.get("cusName")));
            if (snapshot.get("shortName") != null) partner.setShortName(String.valueOf(snapshot.get("shortName")));
            if (snapshot.get("idCode") != null) partner.setIdCode(String.valueOf(snapshot.get("idCode")));
            if (snapshot.get("address") != null) partner.setAddress(String.valueOf(snapshot.get("address")));
            if (snapshot.get("issueBy") != null) partner.setIssueBy(String.valueOf(snapshot.get("issueBy")));
            if (snapshot.get("opLiscenseNo") != null) partner.setOpLiscenseNo(String.valueOf(snapshot.get("opLiscenseNo")));
            if (snapshot.get("mobile") != null) partner.setMobile(String.valueOf(snapshot.get("mobile")));
            if (snapshot.get("email") != null) partner.setEmail(String.valueOf(snapshot.get("email")));
            if (snapshot.get("website") != null) partner.setWebsite(String.valueOf(snapshot.get("website")));
            if (snapshot.get("cusType") != null) partner.setCusType(String.valueOf(snapshot.get("cusType")));
            if (snapshot.get("businessType") != null) partner.setBusinessType(String.valueOf(snapshot.get("businessType")));
            if (snapshot.get("depositoryMemberCode") != null) partner.setDepositoryMemberCode(String.valueOf(snapshot.get("depositoryMemberCode")));
            if (snapshot.get("tradingGateway") != null) partner.setTradingGateway(String.valueOf(snapshot.get("tradingGateway")));
            if (snapshot.get("generalNote") != null) partner.setGeneralNote(String.valueOf(snapshot.get("generalNote")));
            if (snapshot.get("fistIssueDate") != null && !"-".equals(snapshot.get("fistIssueDate"))) {
                try { partner.setFistIssueDate(LocalDate.parse(String.valueOf(snapshot.get("fistIssueDate")))); } catch (Exception ignored) {}
            }
            if (snapshot.get("lastIssueDate") != null && !"-".equals(snapshot.get("lastIssueDate"))) {
                try { partner.setLastIssueDate(LocalDate.parse(String.valueOf(snapshot.get("lastIssueDate")))); } catch (Exception ignored) {}
            }
            if (snapshot.get("opIssueDate") != null && !"-".equals(snapshot.get("opIssueDate"))) {
                try { partner.setOpIssueDate(LocalDate.parse(String.valueOf(snapshot.get("opIssueDate")))); } catch (Exception ignored) {}
            }
            if (snapshot.get("professionalInvestor") != null) {
                partner.setProfessionalInvestor(Boolean.valueOf(String.valueOf(snapshot.get("professionalInvestor"))));
            }
            
            partner.setStatus(Partner.STATUS_APPROVED);
            partner.setChangeReason(null);
            partner.setNote(null);
            partner.setApprovedBy(rejecterId);
            partner.setApprovedAt(java.time.LocalDateTime.now());
            return partnerRepository.save(partner);
        }
        
        // Nếu là từ chối tạo mới lần đầu -> Chuyển sang REJECTED (Hủy bỏ)
        partner.markAsRejected(rejecterId);
        return partnerRepository.save(partner);
    }

    public Partner getPartner(UUID id) {
        return partnerRepository.findById(id);
    }

    public org.springframework.data.domain.Page<Partner> getAllPartners(org.springframework.data.domain.Pageable pageable) {
        return partnerRepository.findAll(pageable);
    }

    // QuÃÂ¡ÃÂºÃÂ£n lÃÆÃÂ½ uÃÂ¡ÃÂ»ÃÂ· quyÃÂ¡ÃÂ»ÃÂn cho ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c
    public Authorization createAuthorization(UUID partnerId, Authorization authorization) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        }
        
        authorization.validateAuthorization();
        
        Integer maxSeq = authorizationRepository.getMaxSeqIdByPartnerId(partnerId);
        authorization.setSeqId(maxSeq + 1);
        authorization.setPartnerId(partnerId);
        authorization.updateStatus();
        if (Partner.STATUS_DRAFT.equals(partner.getStatus()) || Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            authorization.setStatus("PENDING_APPROVAL");
        }
        
        return authorizationRepository.save(authorization);
    }

    public Authorization updateAuthorization(UUID partnerId, UUID authId, Authorization updateRequest) {
        Authorization auth = authorizationRepository.findById(authId);
        if (auth == null || !auth.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.AuthorizationException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y uÃÂ¡ÃÂ»ÃÂ· quyÃÂ¡ÃÂ»ÃÂn");
        }
        
        auth.setAuthName(updateRequest.getAuthName());
        auth.setAuthPosition(updateRequest.getAuthPosition());
        auth.setAuthidNo(updateRequest.getAuthidNo());
        auth.setAuthissueDate(updateRequest.getAuthissueDate());
        auth.setAuthedName(updateRequest.getAuthedName());
        auth.setAuthedIdNo(updateRequest.getAuthedIdNo());
        auth.setAuthedIssueDate(updateRequest.getAuthedIssueDate());
        auth.setIssuePlace(updateRequest.getIssuePlace());
        auth.setAuthNo(updateRequest.getAuthNo());
        auth.setEffDate(updateRequest.getEffDate());
        auth.setExpiryDate(updateRequest.getExpiryDate());
        auth.setAuthedPosition(updateRequest.getAuthedPosition());
        auth.setScope(updateRequest.getScope());
        auth.setPhone(updateRequest.getPhone());
        auth.setEmail(updateRequest.getEmail());
        
        auth.validateAuthorization();
        auth.updateStatus();
        
        return authorizationRepository.save(auth);
    }

    public void deleteAuthorization(UUID partnerId, UUID authId) {
        Authorization auth = authorizationRepository.findById(authId);
        if (auth == null || !auth.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.AuthorizationException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y uÃÂ¡ÃÂ»ÃÂ· quyÃÂ¡ÃÂ»ÃÂn");
        }
        authorizationRepository.deleteById(authId);
    }

    public org.springframework.data.domain.Page<Authorization> getAuthorizationsByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return authorizationRepository.findByPartnerId(partnerId, pageable);
    }

    // QuÃÂ¡ÃÂºÃÂ£n lÃÆÃÂ½ hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c (CreditLimit)
    
    @Transactional
    public CreditLimit createGlobalCreditLimit(vix.local.api.modules.capital_source.api.v1.dto.request.GlobalCreditLimitRequestDto requestDto) {
        Partner partner = partnerRepository.findById(requestDto.getPartnerId());
        if (partner == null) {
            throw new PartnerException("Không tìm thấy đối tác");
        }
        if (!Partner.STATUS_APPROVED.equals(partner.getStatus())) {
            throw new PartnerException("Đối tác chưa được duyệt");
        }

        vix.local.api.modules.capital_source.domain.model.CreditContract contract;
        if (requestDto.getContractId() != null) {
            contract = contractRepository.findById(requestDto.getContractId());
            if (contract == null) {
                throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hợp đồng");
            }
        } else {
            contract = vix.local.api.modules.capital_source.domain.model.CreditContract.builder()
                    .partnerId(requestDto.getPartnerId())
                    .contractNo(requestDto.getContractNo())
                    .contractType(requestDto.getContractType())
                    .totalLimit(requestDto.getContractTotalLimit())
                    .purpose(requestDto.getContractPurpose())
                    .startDate(requestDto.getContractStartDate())
                    .endDate(requestDto.getContractEndDate())
                    .status(vix.local.api.modules.capital_source.domain.model.CreditContract.STATUS_PENDING_APPROVAL)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            contract.initRemain();
            contract = contractRepository.save(contract);
        }

        CreditLimit limit = CreditLimit.builder()
                .partnerId(requestDto.getPartnerId())
                .contractId(contract.getId())
                .limitId(requestDto.getLimitId())
                .poolName(requestDto.getPoolName())
                .currency(requestDto.getCurrency())
                .poolType(requestDto.getPoolType())
                .creditRatio(requestDto.getCreditRatio())
                .purpose(requestDto.getPurpose())
                .totalPool(requestDto.getTotalPool())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .status(CreditLimit.STATUS_PENDING_APPROVAL)
                .createdAt(java.time.LocalDateTime.now())
                .build();


        if (limit.getPoolType() != null && !limit.getPoolType().isEmpty()) {
            limit.setLimitId(contract.getContractNo() + "_" + limit.getPoolType().toUpperCase());
        }

        limit.validateCreditLimit();
        
        if (limit.getTotalPool() != null) {
            limit.setRemainPool(limit.getTotalPool());
        }
        limit.calculateRemainPool();


        return creditLimitRepository.save(limit);
    }


    @Transactional
    public CreditLimit createCreditLimit(UUID contractId, CreditLimit creditLimit) {
        creditLimit.validateCreditLimit();

        vix.local.api.modules.capital_source.domain.model.CreditContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂ»ÃÂ£p ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ång");
        }

        if (creditLimit.getPoolType() != null && !creditLimit.getPoolType().isEmpty()) {
            creditLimit.setLimitId(contract.getContractNo() + "_" + creditLimit.getPoolType().toUpperCase());
        }

        if (creditLimit.getTotalPool() != null) {
            creditLimit.setRemainPool(creditLimit.getTotalPool());
        }

        creditLimit.setStatus(CreditLimit.STATUS_PENDING_APPROVAL);

        creditLimit.setContractId(contractId);
        creditLimit.setPartnerId(contract.getPartnerId());
        return creditLimitRepository.save(creditLimit);
    }
    
    public CreditLimit updateCreditLimit(UUID partnerId, UUID limitId, CreditLimit updateRequest) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }
        
        creditLimit.setPoolName(updateRequest.getPoolName());
        creditLimit.setCurrency(updateRequest.getCurrency());
        creditLimit.setPoolType(updateRequest.getPoolType());
        creditLimit.setStartDate(updateRequest.getStartDate());
        creditLimit.setEndDate(updateRequest.getEndDate());
        
        
        creditLimit.setCreditRatio(updateRequest.getCreditRatio());
        creditLimit.setPurpose(updateRequest.getPurpose());
        
        creditLimit.updateLimitAmount(updateRequest.getTotalPool());
        creditLimit.validateCreditLimit();
        
        return creditLimitRepository.save(creditLimit);
    }
    
    public CreditLimit increaseCreditLimit(UUID partnerId, UUID limitId, BigDecimal amount, String transactionType, String referenceId, UUID creatorId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }

        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("SÃÂ¡ÃÂ»Ã¢â¬Ë tiÃÂ¡ÃÂ»ÃÂn tÃâÃâng phÃÂ¡ÃÂºÃÂ£i lÃÂ¡ÃÂ»Ã¢â¬Âºn hÃâ ÃÂ¡n 0");
        }

        BigDecimal preTotalPool = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : java.math.BigDecimal.ZERO;
        BigDecimal preUsedPool = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : java.math.BigDecimal.ZERO;
        BigDecimal preRemainPool = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : java.math.BigDecimal.ZERO;

        BigDecimal newTotalPool = preTotalPool;
        BigDecimal newUsedPool = preUsedPool;

        if ("DEBT_REPAY".equals(transactionType)) {
            // TrÃÂ¡ÃÂºÃÂ£ nÃÂ¡ÃÂ»ÃÂ£ lÃÆÃÂ m giÃÂ¡ÃÂºÃÂ£m usedPool, totalPool giÃÂ¡ÃÂ»ÃÂ¯ nguyÃÆÃÂªn
            newUsedPool = preUsedPool.subtract(amount);
            if (newUsedPool.compareTo(java.math.BigDecimal.ZERO) < 0) {
                newUsedPool = java.math.BigDecimal.ZERO;
            }
        } else {
            // CÃÆÃÂ¡c giao dÃÂ¡ÃÂ»Ã¢â¬Â¹ch khÃÆÃÂ¡c (MANUAL_INC, ASSET_REVAL_INC, ASSET_MORTGAGE) lÃÆÃÂ m tÃâÃâng totalPool
            newTotalPool = preTotalPool.add(amount);
        }

        BigDecimal newRemainPool = newTotalPool.subtract(newUsedPool);

        // LÃâ ÃÂ°u vÃÆÃÂ o CreditLimit
        creditLimit.setTotalPool(newTotalPool);
        creditLimit.setUsedPool(newUsedPool);
        creditLimit.setRemainPool(newRemainPool);
        CreditLimit savedLimit = creditLimitRepository.save(creditLimit);

        // LÃâ ÃÂ°u lÃÂ¡ÃÂ»Ã¢â¬Â¹ch sÃÂ¡ÃÂ»ÃÂ­
        vix.local.api.modules.capital_source.domain.model.CreditLimitHistory history = vix.local.api.modules.capital_source.domain.model.CreditLimitHistory.builder()
                .creditLimitId(limitId)
                .transactionType(transactionType)
                .amount(amount)
                .preTotalPool(preTotalPool)
                .preUsedPool(preUsedPool)
                .preRemainPool(preRemainPool)
                .newTotalPool(newTotalPool)
                .newUsedPool(newUsedPool)
                .newRemainPool(newRemainPool)
                .referenceId(referenceId)
                .createdBy(creatorId)
                .build();
        creditLimitHistoryRepository.save(history);

        return savedLimit;
    }

    public CreditLimit decreaseCreditLimit(UUID partnerId, UUID limitId, BigDecimal amount, String transactionType, String referenceId, UUID creatorId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }

        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("SÃÂ¡ÃÂ»Ã¢â¬Ë tiÃÂ¡ÃÂ»ÃÂn giÃÂ¡ÃÂºÃÂ£m phÃÂ¡ÃÂºÃÂ£i lÃÂ¡ÃÂ»Ã¢â¬Âºn hÃâ ÃÂ¡n 0");
        }

        BigDecimal preTotalPool = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : java.math.BigDecimal.ZERO;
        BigDecimal preUsedPool = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : java.math.BigDecimal.ZERO;
        BigDecimal preRemainPool = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : java.math.BigDecimal.ZERO;

        BigDecimal newTotalPool = preTotalPool;
        BigDecimal newUsedPool = preUsedPool;

        if ("NEW_LOAN".equals(transactionType)) {
            // GiÃÂ¡ÃÂºÃÂ£i ngÃÆÃÂ¢n KUNN lÃÆÃÂ m tÃâÃâng usedPool
            newUsedPool = preUsedPool.add(amount);
        } else {
            // CÃÆÃÂ¡c giao dÃÂ¡ÃÂ»Ã¢â¬Â¹ch khÃÆÃÂ¡c lÃÆÃÂ m giÃÂ¡ÃÂºÃÂ£m totalPool
            newTotalPool = preTotalPool.subtract(amount);
        }

        BigDecimal newRemainPool = newTotalPool.subtract(newUsedPool);

        // LÃâ ÃÂ°u vÃÆÃÂ o CreditLimit
        creditLimit.setTotalPool(newTotalPool);
        creditLimit.setUsedPool(newUsedPool);
        creditLimit.setRemainPool(newRemainPool);
        CreditLimit savedLimit = creditLimitRepository.save(creditLimit);

        // LÃâ ÃÂ°u lÃÂ¡ÃÂ»Ã¢â¬Â¹ch sÃÂ¡ÃÂ»ÃÂ­
        vix.local.api.modules.capital_source.domain.model.CreditLimitHistory history = vix.local.api.modules.capital_source.domain.model.CreditLimitHistory.builder()
                .creditLimitId(limitId)
                .transactionType(transactionType)
                .amount(amount)
                .preTotalPool(preTotalPool)
                .preUsedPool(preUsedPool)
                .preRemainPool(preRemainPool)
                .newTotalPool(newTotalPool)
                .newUsedPool(newUsedPool)
                .newRemainPool(newRemainPool)
                .referenceId(referenceId)
                .createdBy(creatorId)
                .build();
        creditLimitHistoryRepository.save(history);

        return savedLimit;
    }

    public org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory> searchCreditLimitHistory(
            UUID creditLimitId, java.time.LocalDateTime fromDate, java.time.LocalDateTime toDate, org.springframework.data.domain.Pageable pageable) {
        
        if (fromDate == null && toDate == null) {
            toDate = java.time.LocalDateTime.now();
            fromDate = toDate.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        
        return creditLimitHistoryRepository.findByFilters(creditLimitId, fromDate, toDate, pageable);
    }

    public List<CreditLimit> getAllCreditLimits() {
        return creditLimitRepository.findAll();
    }

    public org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory> searchGlobalCreditLimitHistory(
            UUID partnerId, String contractNo, String limitType, String transactionType,
            java.time.LocalDateTime fromDate, java.time.LocalDateTime toDate, org.springframework.data.domain.Pageable pageable) {
        
        if (fromDate == null && toDate == null) {
            toDate = java.time.LocalDateTime.now();
            fromDate = toDate.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        
        List<UUID> matchingLimitIds = null;
        if (partnerId != null || (contractNo != null && !contractNo.isBlank()) || (limitType != null && !limitType.isBlank())) {
            List<CreditLimit> allLimits = creditLimitRepository.findAll();
            matchingLimitIds = allLimits.stream()
                .filter(l -> partnerId == null || partnerId.equals(l.getPartnerId()))
                .filter(l -> {
                    if (contractNo == null || contractNo.isBlank()) return true;
                    if (l.getContractId() != null) {
                        var contract = contractRepository.findById(l.getContractId());
                        return contract != null && contract.getContractNo() != null && contract.getContractNo().equalsIgnoreCase(contractNo.trim());
                    }
                    return false;
                })
                .filter(l -> {
                    if (limitType == null || limitType.isBlank()) return true;
                    return l.getPoolType() != null && l.getPoolType().equalsIgnoreCase(limitType.trim());
                })
                .map(CreditLimit::getId)
                .toList();

            if (matchingLimitIds.isEmpty()) {
                return org.springframework.data.domain.Page.empty(pageable);
            }
        }
        
        return creditLimitHistoryRepository.findByGlobalFilters(matchingLimitIds, transactionType, fromDate, toDate, pageable);
    }

    public void deleteCreditLimit(UUID partnerId, UUID limitId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }
        
        creditLimit.markAsPendingDelete();
        creditLimitRepository.save(creditLimit);
        
        List<Kunn> kunns = kunnRepository.findByLimitId(limitId);
        kunns.forEach(Kunn::markAsPendingDelete);
        kunnRepository.saveAll(kunns);
    }
    
    public void approveAllCreditLimitsByPartner(UUID partnerId, UUID approverId) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        }
        
        if (Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            partner.markAsApproved(approverId);
            partnerRepository.save(partner);
        }
        
        List<CreditLimit> pendingLimits = creditLimitRepository.findByPartnerIdAndStatus(partnerId, CreditLimit.STATUS_PENDING_APPROVAL);
        if (pendingLimits != null && !pendingLimits.isEmpty()) {
            for (CreditLimit limit : pendingLimits) {
                limit.markAsApproved(approverId);
            }
            creditLimitRepository.saveAll(pendingLimits);
        }
    }
    
    public void rejectAllCreditLimitsByPartner(UUID partnerId, UUID rejecterId) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y ÃâÃ¢â¬ËÃÂ¡ÃÂ»Ã¢â¬Ëi tÃÆÃÂ¡c");
        }
        
        if (Partner.STATUS_PENDING_APPROVAL.equals(partner.getStatus())) {
            partner.markAsRejected(rejecterId);
            partnerRepository.save(partner);
        }
        
        List<CreditLimit> pendingLimits = creditLimitRepository.findByPartnerIdAndStatus(partnerId, CreditLimit.STATUS_PENDING_APPROVAL);
        if (pendingLimits != null && !pendingLimits.isEmpty()) {
            for (CreditLimit limit : pendingLimits) {
                limit.markAsRejected(rejecterId);
            }
            creditLimitRepository.saveAll(pendingLimits);
        }
    }

    public CreditLimit approveCreditLimit(UUID partnerId, UUID limitId, UUID approverId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || (partnerId != null && !creditLimit.getPartnerId().equals(partnerId))) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }
        approveAllCreditLimitsByPartner(creditLimit.getPartnerId(), approverId);
        return creditLimitRepository.findById(limitId);
    }
    
    public CreditLimit rejectCreditLimit(UUID partnerId, UUID limitId, UUID rejecterId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || (partnerId != null && !creditLimit.getPartnerId().equals(partnerId))) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("KhÃÆÃÂ´ng tÃÆÃÂ¬m thÃÂ¡ÃÂºÃÂ¥y hÃÂ¡ÃÂºÃÂ¡n mÃÂ¡ÃÂ»ÃÂ©c");
        }
        rejectAllCreditLimitsByPartner(creditLimit.getPartnerId(), rejecterId);
        return creditLimitRepository.findById(limitId);
    }

    public org.springframework.data.domain.Page<CreditLimit> getCreditLimitsByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return creditLimitRepository.findByPartnerId(partnerId, pageable);
    }

    public org.springframework.data.domain.Page<CreditLimit> searchGlobalCreditLimits(
            UUID partnerId, 
            String limitId, 
            String limitType, 
            String status, 
            java.time.LocalDate startDate, 
            java.time.LocalDate endDate, 
            org.springframework.data.domain.Pageable pageable) {
        return creditLimitRepository.searchGlobal(partnerId, limitId, limitType, status, startDate, endDate, pageable);
    }
    
    public java.util.List<CreditLimit> findChildrenBycontractIds(java.util.List<UUID> parentIds) {
        return creditLimitRepository.findByContractIdIn(parentIds);
    }

    // QuÃƒÂ¡Ã‚ÂºÃ‚Â£n lÃƒÆ’Ã‚Â½ tÃƒÆ’Ã‚Â i sÃƒÂ¡Ã‚ÂºÃ‚Â£n Ãƒâ€žÃ¢â‚¬ËœÃƒÂ¡Ã‚ÂºÃ‚Â£m bÃƒÂ¡Ã‚ÂºÃ‚Â£o cho Ãƒâ€žÃ¢â‚¬ËœÃƒÂ¡Ã‚Â»Ã¢â‚¬Ëœi    // Asset management is moved to AssetApplicationService

    public void approveDeleteCreditLimit(UUID partnerId, UUID limitId) {
        CreditLimit limit = creditLimitRepository.findById(limitId);
        limit.approveDelete();
        creditLimitRepository.save(limit);
        List<Kunn> kunns = kunnRepository.findByLimitId(limitId);
        kunns.forEach(Kunn::forceDelete);
        kunnRepository.saveAll(kunns);
    }

    public void rejectDeleteCreditLimit(UUID partnerId, UUID limitId) {
        CreditLimit limit = creditLimitRepository.findById(limitId);
        limit.rejectDelete();
        creditLimitRepository.save(limit);
        List<Kunn> kunns = kunnRepository.findByLimitId(limitId);
        kunns.forEach(Kunn::markAsActive);
        kunnRepository.saveAll(kunns);
    }

    public void approveDeletePartner(UUID id) {
        Partner partner = partnerRepository.findById(id);
        partner.approveDelete();
        partnerRepository.save(partner);
        org.springframework.data.domain.Page<CreditLimit> limits = creditLimitRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        for (CreditLimit limit : limits.getContent()) {
            limit.forceDelete();
            creditLimitRepository.save(limit);
        }
        List<Kunn> kunns = kunnRepository.findByCusId(id);
        kunns.forEach(Kunn::forceDelete);
        kunnRepository.saveAll(kunns);
    }

    public void rejectDeletePartner(UUID id) {
        Partner partner = partnerRepository.findById(id);
        partner.rejectDelete();
        partnerRepository.save(partner);
        org.springframework.data.domain.Page<CreditLimit> limits = creditLimitRepository.findByPartnerId(id, org.springframework.data.domain.Pageable.unpaged());
        for (CreditLimit limit : limits.getContent()) {
            limit.rejectDelete();
            creditLimitRepository.save(limit);
        }
        List<Kunn> kunns = kunnRepository.findByCusId(id);
        kunns.forEach(Kunn::markAsActive);
        kunnRepository.saveAll(kunns);
    }

    public vix.local.api.modules.capital_source.domain.model.PartnerSignature uploadSignatureFile(UUID partnerId, UUID signatureId, org.springframework.web.multipart.MultipartFile file, UUID companyId, UUID departmentId, UUID updaterId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null) return null;
        
        vix.local.api.modules.document.domain.model.Document document = documentPort.upload(file, companyId, departmentId, updaterId != null ? updaterId.toString() : "system");
        
        signature.setDocumentId(document.getId());
        signature.setSignFileName(document.getName());
        signature.setUpdatedBy(updaterId);
        signature.updateStatus();
        return partnerSignatureRepository.save(signature);
    }

    public String getSignaturePreviewUrl(UUID partnerId, UUID signatureId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || signature.getDocumentId() == null) {
            return null;
        }
        return documentPort.getDownloadUrl(signature.getDocumentId());
    }

    public org.springframework.core.io.Resource getSignatureFileResource(UUID partnerId, UUID signatureId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || signature.getDocumentId() == null) {
            return null;
        }
        java.io.InputStream is = documentPort.loadDocument(signature.getDocumentId());
        return new org.springframework.core.io.InputStreamResource(is);
    }

    public vix.local.api.modules.document.domain.model.Document getSignatureDocument(UUID partnerId, UUID signatureId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || signature.getDocumentId() == null) {
            return null;
        }
        return documentPort.getDocumentById(signature.getDocumentId());
    }

    public Partner setPartnerPool(UUID partnerId, java.math.BigDecimal totalPool) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) throw new PartnerException("KhÃ´ng tÃ¬m tháº¥y Äá»i tÃ¡c");
        partner.setTotalPool(totalPool);
        if (partner.getUsedPool() == null) partner.setUsedPool(java.math.BigDecimal.ZERO);
        partner.setRemainPool(totalPool.subtract(partner.getUsedPool()));
        return partnerRepository.save(partner);
    }


}







