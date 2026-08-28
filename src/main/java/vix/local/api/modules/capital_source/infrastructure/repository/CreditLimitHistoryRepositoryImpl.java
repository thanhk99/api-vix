package vix.local.api.modules.capital_source.infrastructure.repository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.domain.model.CreditLimitHistory;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.CreditLimitHistoryEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        Specification<CreditLimitHistoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (creditLimitId != null) {
                predicates.add(cb.equal(root.get("creditLimitId"), creditLimitId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), toDate));
            }
            query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jpaRepository.findAll(spec, pageable).map(this::toModel);
    }

    @Override
    public Page<CreditLimitHistory> findByGlobalFilters(List<UUID> creditLimitIds, String transactionType, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Specification<CreditLimitHistoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (creditLimitIds != null && !creditLimitIds.isEmpty()) {
                predicates.add(root.get("creditLimitId").in(creditLimitIds));
            }
            if (transactionType != null && !transactionType.isBlank()) {
                String pattern = "%" + transactionType.trim().toLowerCase() + "%";
                Predicate matchType = cb.like(cb.lower(root.get("transactionType")), pattern);
                Predicate matchRef = cb.like(cb.lower(root.get("referenceId")), pattern);
                predicates.add(cb.or(matchType, matchRef));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), toDate));
            }
            query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jpaRepository.findAll(spec, pageable).map(this::toModel);
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
        if (entity == null) return null;
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
