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
        entity.setIssueBy(partner.getIssueBy());
        entity.setChangeCount(partner.getChangeCount());
        entity.setOpLiscenseNo(partner.getOpLiscenseNo());
        entity.setOpIssueDate(partner.getOpIssueDate());
        entity.setMobile(partner.getMobile());
        entity.setEmail(partner.getEmail());
        entity.setWebsite(partner.getWebsite());
        entity.setCusType(partner.getCusType());
        entity.setBusinessType(partner.getBusinessType());
        entity.setProfessionalInvestor(partner.getProfessionalInvestor());
        entity.setProfessionalStartDate(partner.getProfessionalStartDate());
        entity.setProfessionalEndDate(partner.getProfessionalEndDate());
        entity.setNote(partner.getNote());
        entity.setStatus(partner.getStatus());
        entity.setCreatedBy(partner.getCreatedBy());
        entity.setUpdatedBy(partner.getUpdatedBy());
        entity.setLastUpdated(partner.getLastUpdated());
        entity.setApprovedBy(partner.getApprovedBy());
        entity.setApprovedAt(partner.getApprovedAt());
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
                .issueBy(entity.getIssueBy())
                .changeCount(entity.getChangeCount())
                .opLiscenseNo(entity.getOpLiscenseNo())
                .opIssueDate(entity.getOpIssueDate())
                .mobile(entity.getMobile())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .cusType(entity.getCusType())
                .businessType(entity.getBusinessType())
                .professionalInvestor(entity.getProfessionalInvestor())
                .professionalStartDate(entity.getProfessionalStartDate())
                .professionalEndDate(entity.getProfessionalEndDate())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .lastUpdated(entity.getLastUpdated())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}