package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitEntity;
import java.util.List;
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
    public List<CreditLimit> findByPartnerId(UUID partnerId) {
        return creditLimitJpaRepository.findByPartnerId(partnerId).stream()
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
                .limitId(creditLimit.getLimitId())
                .poolName(creditLimit.getPoolName())
                .currency(creditLimit.getCurrency())
                .poolType(creditLimit.getPoolType())
                .totalPool(creditLimit.getTotalPool())
                .usedPool(creditLimit.getUsedPool())
                .remainPool(creditLimit.getRemainPool())
                .startDate(creditLimit.getStartDate())
                .endDate(creditLimit.getEndDate())
                .status(creditLimit.getStatus())
                .build();
        return entity;
    }

    private CreditLimit convertToModel(CreditLimitEntity entity) {
        if (entity == null) return null;
        return CreditLimit.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .limitId(entity.getLimitId())
                .poolName(entity.getPoolName())
                .currency(entity.getCurrency())
                .poolType(entity.getPoolType())
                .totalPool(entity.getTotalPool())
                .usedPool(entity.getUsedPool())
                .remainPool(entity.getRemainPool())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .build();
    }
}