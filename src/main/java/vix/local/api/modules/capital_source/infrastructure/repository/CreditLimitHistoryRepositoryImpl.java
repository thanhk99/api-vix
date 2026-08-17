package vix.local.api.modules.capital_source.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.domain.model.CreditLimitHistory;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitHistoryEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreditLimitHistoryRepositoryImpl implements CreditLimitHistoryRepository {
    private final JpaCreditLimitHistoryRepository jpaRepository;

    @Override
    public CreditLimitHistory save(CreditLimitHistory history) {
        CreditLimitHistoryEntity entity = toEntity(history);
        CreditLimitHistoryEntity saved = jpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Page<CreditLimitHistory> findByCreditLimitId(UUID creditLimitId, Pageable pageable) {
        return jpaRepository.findByCreditLimitIdOrderByTransactionDateDesc(creditLimitId, pageable)
                .map(this::toModel);
    }

    @Override
    public Page<CreditLimitHistory> findByFilters(UUID creditLimitId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        return jpaRepository.findByFilters(creditLimitId, fromDate, toDate, pageable)
                .map(this::toModel);
    }

    private CreditLimitHistoryEntity toEntity(CreditLimitHistory model) {
        return CreditLimitHistoryEntity.builder()
                .id(model.getId())
                .creditLimitId(model.getCreditLimitId())
                .transactionType(model.getTransactionType())
                .amount(model.getAmount())
                .preTotalPool(model.getPreTotalPool())
                .preUsedPool(model.getPreUsedPool())
                .preRemainPool(model.getPreRemainPool())
                .newTotalPool(model.getNewTotalPool())
                .newUsedPool(model.getNewUsedPool())
                .newRemainPool(model.getNewRemainPool())
                .transactionDate(model.getTransactionDate())
                .referenceId(model.getReferenceId())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .build();
    }

    private CreditLimitHistory toModel(CreditLimitHistoryEntity entity) {
        return CreditLimitHistory.builder()
                .id(entity.getId())
                .creditLimitId(entity.getCreditLimitId())
                .transactionType(entity.getTransactionType())
                .amount(entity.getAmount())
                .preTotalPool(entity.getPreTotalPool())
                .preUsedPool(entity.getPreUsedPool())
                .preRemainPool(entity.getPreRemainPool())
                .newTotalPool(entity.getNewTotalPool())
                .newUsedPool(entity.getNewUsedPool())
                .newRemainPool(entity.getNewRemainPool())
                .transactionDate(entity.getTransactionDate())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
