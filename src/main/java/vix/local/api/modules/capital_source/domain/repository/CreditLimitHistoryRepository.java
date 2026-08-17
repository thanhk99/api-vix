package vix.local.api.modules.capital_source.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.capital_source.domain.model.CreditLimitHistory;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreditLimitHistoryRepository {
    CreditLimitHistory save(CreditLimitHistory history);
    
    Page<CreditLimitHistory> findByCreditLimitId(UUID creditLimitId, Pageable pageable);
    
    Page<CreditLimitHistory> findByFilters(UUID creditLimitId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
