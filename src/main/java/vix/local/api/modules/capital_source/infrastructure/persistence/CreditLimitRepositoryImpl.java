package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitEntity;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CreditLimitRepositoryImpl implements CreditLimitRepository {

    private final CreditLimitJpaRepository creditLimitJpaRepository;

    @Override
    public CreditLimit save(CreditLimit creditLimit) {
        CreditLimitEntity entity = convertToEntity(creditLimit);
        CreditLimitEntity saved = creditLimitJpaRepository.save(entity);
        return convertToModel(saved);
    }

    @Override
    public Page<CreditLimit> findByPartnerId(UUID partnerId, Pageable pageable) {
        return creditLimitJpaRepository.findByPartnerId(partnerId, pageable)
                .map(this::convertToModel);
    }

    @Override
    public Page<CreditLimit> searchGlobal(
            UUID partnerId, 
            String limitId, 
            String contactNo, 
            String poolType, 
            String status, 
            java.time.LocalDate startDate, 
            java.time.LocalDate endDate, 
            Pageable pageable) {
        return creditLimitJpaRepository.searchGlobal(partnerId, limitId, contactNo, poolType, status, startDate, endDate, pageable)
                .map(this::convertToModel);
    }

    @Override
    public java.util.List<CreditLimit> findByParentIdIn(java.util.List<UUID> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) return java.util.Collections.emptyList();
        return creditLimitJpaRepository.findByParentIdInAndStatusNot(parentIds, vix.local.api.modules.capital_source.domain.model.CreditLimit.STATUS_DELETED).stream()
                .map(this::convertToModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        creditLimitJpaRepository.deleteById(id);
    }

    @Override
    public CreditLimit findById(UUID id) {
        return convertToModel(creditLimitJpaRepository.findById(id).orElse(null));
    }

    private CreditLimitEntity convertToEntity(CreditLimit creditLimit) {
        CreditLimitEntity entity = CreditLimitEntity.builder()
                .id(creditLimit.getId())
                .partnerId(creditLimit.getPartnerId())
                .parentId(creditLimit.getParentId())
                .limitId(creditLimit.getLimitId())
                .poolName(creditLimit.getPoolName())
                .currency(creditLimit.getCurrency())
                .poolType(creditLimit.getPoolType())
                .contactNo(creditLimit.getContactNo())
                .creditRatio(creditLimit.getCreditRatio())
                .purpose(creditLimit.getPurpose())
                .totalPool(creditLimit.getTotalPool())
                .usedPool(creditLimit.getUsedPool())
                .remainPool(creditLimit.getRemainPool())
                .startDate(creditLimit.getStartDate())
                .endDate(creditLimit.getEndDate())
                .status(creditLimit.getStatus())
                .createdAt(creditLimit.getCreatedAt())
                .updatedAt(creditLimit.getUpdatedAt())
                .approvedBy(creditLimit.getApprovedBy())
                .approvedAt(creditLimit.getApprovedAt())
                .build();
        return entity;
    }

    private CreditLimit convertToModel(CreditLimitEntity entity) {
        if (entity == null)
            return null;
        return CreditLimit.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .parentId(entity.getParentId())
                .limitId(entity.getLimitId())
                .poolName(entity.getPoolName())
                .currency(entity.getCurrency())
                .poolType(entity.getPoolType())
                .contactNo(entity.getContactNo())
                .creditRatio(entity.getCreditRatio())
                .purpose(entity.getPurpose())
                .totalPool(entity.getTotalPool())
                .usedPool(entity.getUsedPool())
                .remainPool(entity.getRemainPool())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}