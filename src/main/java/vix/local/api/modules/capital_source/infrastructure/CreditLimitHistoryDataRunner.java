package vix.local.api.modules.capital_source.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.CreditLimitHistory;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitHistoryRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditLimitHistoryDataRunner implements CommandLineRunner {

    private final CreditLimitRepository creditLimitRepository;
    private final CreditLimitHistoryRepository creditLimitHistoryRepository;

    @Override
    public void run(String... args) {
        try {
            List<CreditLimit> limits = creditLimitRepository.findAll();
            if (limits.isEmpty()) return;

            for (CreditLimit limit : limits) {
                var existingHistory = creditLimitHistoryRepository.findByFilters(limit.getId(), null, null, org.springframework.data.domain.PageRequest.of(0, 1));
                if (existingHistory.isEmpty()) {
                    LocalDateTime createdDate = limit.getCreatedAt() != null ? limit.getCreatedAt() : LocalDateTime.now().minusDays(3);
                    BigDecimal total = limit.getTotalPool() != null ? limit.getTotalPool() : BigDecimal.ZERO;
                    BigDecimal used = limit.getUsedPool() != null ? limit.getUsedPool() : BigDecimal.ZERO;
                    BigDecimal remain = limit.getRemainPool() != null ? limit.getRemainPool() : total.subtract(used);

                    // 1. Initial setup record
                    CreditLimitHistory initialHist = CreditLimitHistory.builder()
                            .creditLimitId(limit.getId())
                            .transactionType("INITIAL_SETUP")
                            .amount(total)
                            .preTotalPool(BigDecimal.ZERO)
                            .preUsedPool(BigDecimal.ZERO)
                            .preRemainPool(BigDecimal.ZERO)
                            .newTotalPool(total)
                            .newUsedPool(BigDecimal.ZERO)
                            .newRemainPool(total)
                            .referenceId("Thiết lập hạn mức ban đầu")
                            .transactionDate(createdDate)
                            .createdAt(createdDate)
                            .build();
                    creditLimitHistoryRepository.save(initialHist);

                    // 2. If limit was used, log a KUNN disbursement transaction
                    if (used.compareTo(BigDecimal.ZERO) > 0) {
                        CreditLimitHistory kunnHist = CreditLimitHistory.builder()
                                .creditLimitId(limit.getId())
                                .transactionType("NEW_LOAN")
                                .amount(used)
                                .preTotalPool(total)
                                .preUsedPool(BigDecimal.ZERO)
                                .preRemainPool(total)
                                .newTotalPool(total)
                                .newUsedPool(used)
                                .newRemainPool(remain)
                                .referenceId("Phát sinh KUNN (Giải ngân)")
                                .transactionDate(createdDate.plusHours(2))
                                .createdAt(createdDate.plusHours(2))
                                .build();
                        creditLimitHistoryRepository.save(kunnHist);
                    }
                    log.info("Initialized real history for credit limit: {}", limit.getId());
                }
            }
        } catch (Exception e) {
            log.warn("Could not auto-populate initial history records: {}", e.getMessage());
        }
    }
}
