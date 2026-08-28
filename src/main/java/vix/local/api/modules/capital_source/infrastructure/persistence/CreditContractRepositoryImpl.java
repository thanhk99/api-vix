package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.CreditContract;
import vix.local.api.modules.capital_source.domain.repository.CreditContractRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditContractEntity;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CreditContractRepositoryImpl implements CreditContractRepository {

    private final CreditContractJpaRepository jpaRepository;

    @Override
    public CreditContract save(CreditContract contract) {
        return toModel(jpaRepository.save(toEntity(contract)));
    }

    @Override
    public CreditContract findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel).orElse(null);
    }

    @Override
    public Page<CreditContract> findByPartnerId(UUID partnerId, Pageable pageable) {
        return jpaRepository.findByPartnerId(partnerId, pageable).map(this::toModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public java.util.List<CreditContract> findByPartnerIdAndStatus(UUID partnerId, String status) {
        return jpaRepository.findByPartnerIdAndStatus(partnerId, status).stream().map(this::toModel).toList();
    }

    private CreditContractEntity toEntity(CreditContract model) {
        if (model == null) return null;
        return CreditContractEntity.builder()
                .id(model.getId())
                .partnerId(model.getPartnerId())
                .contractNo(model.getContractNo())
                .totalLimit(model.getTotalLimit())
                .usedLimit(model.getUsedLimit())
                .remainLimit(model.getRemainLimit())
                .purpose(model.getPurpose())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .approvedBy(model.getApprovedBy())
                .approvedAt(model.getApprovedAt())
                .build();
    }

    private CreditContract toModel(CreditContractEntity entity) {
        if (entity == null) return null;
        return CreditContract.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .contractNo(entity.getContractNo())
                .totalLimit(entity.getTotalLimit())
                .usedLimit(entity.getUsedLimit())
                .remainLimit(entity.getRemainLimit())
                .purpose(entity.getPurpose())
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