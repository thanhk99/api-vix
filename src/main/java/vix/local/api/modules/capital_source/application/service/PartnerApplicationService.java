package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.domain.repository.AuthorizationRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PartnerApplicationService {

    private final PartnerRepository partnerRepository;
    private final AuthorizationRepository authorizationRepository;
    private final CreditLimitRepository creditLimitRepository;
    private final CreditLimitHistoryRepository creditLimitHistoryRepository;
    private final AssetRepository assetRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerSignatureRepository partnerSignatureRepository;


    // Quản lý chữ ký cho đối tác
    public vix.local.api.modules.capital_source.domain.model.PartnerSignature createSignature(UUID partnerId, vix.local.api.modules.capital_source.domain.model.PartnerSignature signature, UUID updaterId) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        signature.setPartnerId(partnerId);
        signature.setUpdatedBy(updaterId);
        signature.validateSignature();
        signature.updateStatus();
        return partnerSignatureRepository.save(signature);
    }

    public vix.local.api.modules.capital_source.domain.model.PartnerSignature updateSignature(UUID partnerId, UUID signatureId, vix.local.api.modules.capital_source.domain.model.PartnerSignature updateRequest, UUID updaterId) {
        vix.local.api.modules.capital_source.domain.model.PartnerSignature signature = partnerSignatureRepository.findById(signatureId);
        if (signature == null || !signature.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException("Không tìm thấy chữ ký");
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
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException("Không tìm thấy chữ ký");
        }
        partnerSignatureRepository.deleteById(signatureId);
    }

    public org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.PartnerSignature> getSignaturesByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return partnerSignatureRepository.findByPartnerId(partnerId, pageable);
    }

    public Partner createPartner(Partner partner) {
        // Validate partner
        partner.validatePartner();

        // Đặt trạng thái mặc định là chờ duyệt
        partner.setStatus(Partner.STATUS_PENDING_APPROVAL);

        // Tạo đối tác mới
        return partnerRepository.save(partner);
    }
    
    public Partner updatePartner(UUID id, Partner updateRequest) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        
        partner.updatePartnerInfo(updateRequest.getCusId(), updateRequest.getCusName());
        
        // Cập nhật các trường khác
        partner.setBranchCusId(updateRequest.getBranchCusId());
        partner.setShortName(updateRequest.getShortName());
        partner.setAddress(updateRequest.getAddress());
        partner.setIdCode(updateRequest.getIdCode());
        partner.setFistIssueDate(updateRequest.getFistIssueDate());
        partner.setLastIssueDate(updateRequest.getLastIssueDate());
        partner.setIssueBy(updateRequest.getIssueBy());
        partner.setChangeCount(updateRequest.getChangeCount());
        partner.setOpLiscenseNo(updateRequest.getOpLiscenseNo());
        partner.setOpIssueDate(updateRequest.getOpIssueDate());
        partner.setMobile(updateRequest.getMobile());
        partner.setEmail(updateRequest.getEmail());
        partner.setWebsite(updateRequest.getWebsite());
        
        partner.validatePartner();
        
        return partnerRepository.save(partner);
    }
    
    public Partner updateCustomerType(UUID id, Partner updateRequest) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
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
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        
        partner.markAsDeleted();
        partnerRepository.save(partner);
    }
    
    public Partner approvePartner(UUID id, UUID approverId) {
        Partner partner = partnerRepository.findById(id);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        
        partner.markAsApproved(approverId);
        return partnerRepository.save(partner);
    }

    public Partner getPartner(UUID id) {
        return partnerRepository.findById(id);
    }

    public org.springframework.data.domain.Page<Partner> getAllPartners(org.springframework.data.domain.Pageable pageable) {
        return partnerRepository.findAll(pageable);
    }

    // Quản lý uỷ quyền cho đối tác
    public Authorization createAuthorization(UUID partnerId, Authorization authorization) {
        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }
        
        authorization.validateAuthorization();
        
        Integer maxSeq = authorizationRepository.getMaxSeqIdByPartnerId(partnerId);
        authorization.setSeqId(maxSeq + 1);
        authorization.setPartnerId(partnerId);
        authorization.updateStatus();
        
        return authorizationRepository.save(authorization);
    }

    public Authorization updateAuthorization(UUID partnerId, UUID authId, Authorization updateRequest) {
        Authorization auth = authorizationRepository.findById(authId);
        if (auth == null || !auth.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.AuthorizationException("Không tìm thấy uỷ quyền");
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
            throw new vix.local.api.modules.capital_source.domain.exception.AuthorizationException("Không tìm thấy uỷ quyền");
        }
        authorizationRepository.deleteById(authId);
    }

    public org.springframework.data.domain.Page<Authorization> getAuthorizationsByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return authorizationRepository.findByPartnerId(partnerId, pageable);
    }

    // Quản lý hạn mức cho đối tác
    public CreditLimit createCreditLimit(UUID partnerId, CreditLimit creditLimit) {
        // Validate credit limit
        creditLimit.validateCreditLimit();

        Partner partner = partnerRepository.findById(partnerId);
        if (partner == null) {
            throw new vix.local.api.modules.capital_source.domain.exception.PartnerException("Không tìm thấy đối tác");
        }

        // Tự động sinh limitId nếu có poolType
        if (creditLimit.getPoolType() != null && !creditLimit.getPoolType().isEmpty()) {
            String branchCusId = partner.getBranchCusId() != null ? partner.getBranchCusId() : "UNKNOWN";
            creditLimit.setLimitId(branchCusId + "_" + creditLimit.getPoolType().toUpperCase());
        }


        // Tính toán remainPool
        if (creditLimit.getTotalPool() != null) {
            creditLimit.setRemainPool(creditLimit.getTotalPool());
        }

        // Đặt trạng thái mặc định là chờ duyệt
        creditLimit.setStatus(CreditLimit.STATUS_PENDING_APPROVAL);

        // Gán partnerId và lưu
        creditLimit.setPartnerId(partnerId);
        return creditLimitRepository.save(creditLimit);
    }
    
    public CreditLimit updateCreditLimit(UUID partnerId, UUID limitId, CreditLimit updateRequest) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hạn mức");
        }
        
        creditLimit.setPoolName(updateRequest.getPoolName());
        creditLimit.setCurrency(updateRequest.getCurrency());
        creditLimit.setPoolType(updateRequest.getPoolType());
        creditLimit.setStartDate(updateRequest.getStartDate());
        creditLimit.setEndDate(updateRequest.getEndDate());
        
        creditLimit.setContactNo(updateRequest.getContactNo());
        creditLimit.setCreditRatio(updateRequest.getCreditRatio());
        creditLimit.setPurpose(updateRequest.getPurpose());
        
        creditLimit.updateLimitAmount(updateRequest.getTotalPool());
        creditLimit.validateCreditLimit();
        
        return creditLimitRepository.save(creditLimit);
    }
    
    public CreditLimit increaseCreditLimit(UUID partnerId, UUID limitId, BigDecimal amount, String transactionType, String referenceId, UUID creatorId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hạn mức");
        }

        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Số tiền tăng phải lớn hơn 0");
        }

        BigDecimal preTotalPool = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : java.math.BigDecimal.ZERO;
        BigDecimal preUsedPool = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : java.math.BigDecimal.ZERO;
        BigDecimal preRemainPool = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : java.math.BigDecimal.ZERO;

        BigDecimal newTotalPool = preTotalPool;
        BigDecimal newUsedPool = preUsedPool;

        if ("DEBT_REPAY".equals(transactionType)) {
            // Trả nợ làm giảm usedPool, totalPool giữ nguyên
            newUsedPool = preUsedPool.subtract(amount);
            if (newUsedPool.compareTo(java.math.BigDecimal.ZERO) < 0) {
                newUsedPool = java.math.BigDecimal.ZERO;
            }
        } else {
            // Các giao dịch khác (MANUAL_INC, ASSET_REVAL_INC, ASSET_MORTGAGE) làm tăng totalPool
            newTotalPool = preTotalPool.add(amount);
        }

        BigDecimal newRemainPool = newTotalPool.subtract(newUsedPool);

        // Lưu vào CreditLimit
        creditLimit.setTotalPool(newTotalPool);
        creditLimit.setUsedPool(newUsedPool);
        creditLimit.setRemainPool(newRemainPool);
        CreditLimit savedLimit = creditLimitRepository.save(creditLimit);

        // Lưu lịch sử
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
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hạn mức");
        }

        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Số tiền giảm phải lớn hơn 0");
        }

        BigDecimal preTotalPool = creditLimit.getTotalPool() != null ? creditLimit.getTotalPool() : java.math.BigDecimal.ZERO;
        BigDecimal preUsedPool = creditLimit.getUsedPool() != null ? creditLimit.getUsedPool() : java.math.BigDecimal.ZERO;
        BigDecimal preRemainPool = creditLimit.getRemainPool() != null ? creditLimit.getRemainPool() : java.math.BigDecimal.ZERO;

        BigDecimal newTotalPool = preTotalPool;
        BigDecimal newUsedPool = preUsedPool;

        if ("NEW_LOAN".equals(transactionType)) {
            // Giải ngân KUNN làm tăng usedPool
            newUsedPool = preUsedPool.add(amount);
        } else {
            // Các giao dịch khác làm giảm totalPool
            newTotalPool = preTotalPool.subtract(amount);
        }

        BigDecimal newRemainPool = newTotalPool.subtract(newUsedPool);

        // Lưu vào CreditLimit
        creditLimit.setTotalPool(newTotalPool);
        creditLimit.setUsedPool(newUsedPool);
        creditLimit.setRemainPool(newRemainPool);
        CreditLimit savedLimit = creditLimitRepository.save(creditLimit);

        // Lưu lịch sử
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

    public void deleteCreditLimit(UUID partnerId, UUID limitId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hạn mức");
        }
        
        creditLimit.markAsDeleted();
        creditLimitRepository.save(creditLimit);
    }
    
    public CreditLimit approveCreditLimit(UUID partnerId, UUID limitId, UUID approverId) {
        CreditLimit creditLimit = creditLimitRepository.findById(limitId);
        if (creditLimit == null || !creditLimit.getPartnerId().equals(partnerId)) {
            throw new vix.local.api.modules.capital_source.domain.exception.CreditLimitException("Không tìm thấy hạn mức");
        }
        
        creditLimit.markAsApproved(approverId);
        return creditLimitRepository.save(creditLimit);
    }

    public org.springframework.data.domain.Page<CreditLimit> getCreditLimitsByPartnerId(UUID partnerId, org.springframework.data.domain.Pageable pageable) {
        return creditLimitRepository.findByPartnerId(partnerId, pageable);
    }

    public org.springframework.data.domain.Page<CreditLimit> searchGlobalCreditLimits(
            UUID partnerId, 
            String limitId, 
            String contactNo, 
            String limitType, 
            String status, 
            java.time.LocalDate startDate, 
            java.time.LocalDate endDate, 
            org.springframework.data.domain.Pageable pageable) {
        return creditLimitRepository.searchGlobal(partnerId, limitId, contactNo, limitType, status, startDate, endDate, pageable);
    }
    
    public java.util.List<CreditLimit> findChildrenByParentIds(java.util.List<UUID> parentIds) {
        return creditLimitRepository.findByParentIdIn(parentIds);
    }

    // Quản lý tài sản đảm bảo cho đối tác
    public Asset createAsset(UUID partnerId, Asset asset) {
        // Validate asset
        asset.validateAsset();

        // Gán partnerId và lưu
        asset.setPartnerId(partnerId);
        return assetRepository.save(asset);
    }

    public List<Asset> getAssetsByPartnerId(UUID partnerId) {
        return assetRepository.findByPartnerId(partnerId);
    }

    public List<Asset> getAssetsByCreditLimitId(UUID creditLimitId) {
        return assetRepository.findByCreditLimitId(creditLimitId);
    }
}