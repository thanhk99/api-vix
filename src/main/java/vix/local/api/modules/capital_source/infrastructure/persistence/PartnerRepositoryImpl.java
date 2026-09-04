package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerEntity;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartnerRepositoryImpl implements PartnerRepository {
    
    private final PartnerJpaRepository partnerJpaRepository;
    
    @Override
    public Partner save(Partner partner) {
        PartnerEntity entity = convertToEntity(partner);
        PartnerEntity saved = partnerJpaRepository.save(entity);
        return convertToModel(saved);
    }
    
    @Override
    public Partner findById(UUID id) {
        return convertToModel(partnerJpaRepository.findById(id).orElse(null));
    }
    
    @Override
    public Page<Partner> findAll(Pageable pageable) {
        return partnerJpaRepository.findAllActive(pageable)
                .map(this::convertToModel);
    }
    
    @Override
    public void deleteById(UUID id) {
        partnerJpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByCusId(String cusId) {
        if (cusId == null || cusId.trim().isEmpty()) return false;
        return partnerJpaRepository.existsByCusIdIgnoreCase(cusId.trim());
    }

    @Override
    public boolean existsByCusIdAndIdNot(String cusId, UUID id) {
        if (cusId == null || cusId.trim().isEmpty() || id == null) return false;
        return partnerJpaRepository.existsByCusIdIgnoreCaseAndIdNot(cusId.trim(), id);
    }

    @Override
    public boolean existsByBranchCusId(String branchCusId) {
        if (branchCusId == null || branchCusId.trim().isEmpty()) return false;
        return partnerJpaRepository.existsByBranchCusIdIgnoreCase(branchCusId.trim());
    }

    @Override
    public boolean existsByBranchCusIdAndIdNot(String branchCusId, UUID id) {
        if (branchCusId == null || branchCusId.trim().isEmpty() || id == null) return false;
        return partnerJpaRepository.existsByBranchCusIdIgnoreCaseAndIdNot(branchCusId.trim(), id);
    }
    
    private PartnerEntity convertToEntity(Partner partner) {
        PartnerEntity entity = new PartnerEntity();
        entity.setId(partner.getId());
        entity.setCusId(partner.getCusId());
        entity.setBranchCusId(partner.getBranchCusId());
        entity.setCusName(partner.getCusName());
        entity.setShortName(partner.getShortName());
        entity.setAddress(partner.getAddress());
        entity.setIdCode(partner.getIdCode());
        entity.setFistIssueDate(partner.getFistIssueDate());
        entity.setLastIssueDate(partner.getLastIssueDate());
        entity.setChangeReason(partner.getChangeReason());
        entity.setIssueBy(partner.getIssueBy());
        entity.setChangeCount(partner.getChangeCount());
        entity.setOpLiscenseNo(partner.getOpLiscenseNo());
        entity.setOpIssueDate(partner.getOpIssueDate());
        entity.setOpIssueBy(partner.getOpIssueBy());
        entity.setMobile(partner.getMobile());
        entity.setEmail(partner.getEmail());
        entity.setWebsite(partner.getWebsite());
        entity.setFax(partner.getFax());
        entity.setGeneralNote(partner.getGeneralNote());
        entity.setCusType(partner.getCusType());
        entity.setBusinessType(partner.getBusinessType());
        entity.setProfessionalInvestor(partner.getProfessionalInvestor());
        entity.setProfessionalStartDate(partner.getProfessionalStartDate());
        entity.setProfessionalEndDate(partner.getProfessionalEndDate());
        entity.setNote(partner.getNote());
        entity.setDepositoryMemberCode(partner.getDepositoryMemberCode());
        entity.setTradingGateway(partner.getTradingGateway());
        entity.setStatus(partner.getStatus());
        entity.setIsActive(partner.getIsActive());
        entity.setCreatedBy(partner.getCreatedBy());
        entity.setUpdatedBy(partner.getUpdatedBy());
        entity.setLastUpdated(partner.getLastUpdated());
        entity.setApprovedBy(partner.getApprovedBy());
        entity.setApprovedAt(partner.getApprovedAt());
        entity.setTotalPool(partner.getTotalPool());
        entity.setUsedPool(partner.getUsedPool());
        entity.setRemainPool(partner.getRemainPool());
        return entity;
    }
    
    private Partner convertToModel(PartnerEntity entity) {
        if (entity == null) return null;
        return Partner.builder()
                .id(entity.getId())
                .cusId(entity.getCusId())
                .branchCusId(entity.getBranchCusId())
                .cusName(entity.getCusName())
                .shortName(entity.getShortName())
                .address(entity.getAddress())
                .idCode(entity.getIdCode())
                .fistIssueDate(entity.getFistIssueDate())
                .lastIssueDate(entity.getLastIssueDate())
                .changeReason(entity.getChangeReason())
                .issueBy(entity.getIssueBy())
                .changeCount(entity.getChangeCount())
                .opLiscenseNo(entity.getOpLiscenseNo())
                .opIssueDate(entity.getOpIssueDate())
                .opIssueBy(entity.getOpIssueBy())
                .mobile(entity.getMobile())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .fax(entity.getFax())
                .generalNote(entity.getGeneralNote())
                .cusType(entity.getCusType())
                .businessType(entity.getBusinessType())
                .professionalInvestor(entity.getProfessionalInvestor())
                .professionalStartDate(entity.getProfessionalStartDate())
                .professionalEndDate(entity.getProfessionalEndDate())
                .note(entity.getNote())
                .depositoryMemberCode(entity.getDepositoryMemberCode())
                .tradingGateway(entity.getTradingGateway())
                .status(entity.getStatus())
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .lastUpdated(entity.getLastUpdated())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .totalPool(entity.getTotalPool())
                .usedPool(entity.getUsedPool())
                .remainPool(entity.getRemainPool())
                .build();
    }
}
