package vix.local.api.modules.capital_source.domain.model;

import org.junit.jupiter.api.Test;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreditLimitTest {

    @Test
    void validateCreditLimit_shouldPass_whenValid() {
        CreditLimit limit = CreditLimit.builder()
                .totalPool(new BigDecimal("1000"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .poolType("NORMAL")
                .purpose("Purpose A, Purpose B")
                .build();
        
        assertDoesNotThrow(limit::validateCreditLimit);
    }

    @Test
    void validateCreditLimit_shouldThrow_whenMarginHasMultiplePurposes() {
        CreditLimit limit = CreditLimit.builder()
                .totalPool(new BigDecimal("1000"))
                .startDate(LocalDate.now())
                .poolType("MARGIN")
                .purpose("Purpose A, Purpose B")
                .build();
        
        CreditLimitException exception = assertThrows(CreditLimitException.class, limit::validateCreditLimit);
        assertEquals("Hạn mức loại MARGIN chỉ được chọn 1 mục đích", exception.getMessage());
    }

    @Test
    void validateCreditLimit_shouldPass_whenMarginHasSinglePurpose() {
        CreditLimit limit = CreditLimit.builder()
                .totalPool(new BigDecimal("1000"))
                .startDate(LocalDate.now())
                .poolType("MARGIN")
                .purpose("Purpose A")
                .build();
        
        assertDoesNotThrow(limit::validateCreditLimit);
    }

    @Test
    void validateCreditLimit_shouldThrow_whenTotalPoolIsZero() {
        CreditLimit limit = CreditLimit.builder()
                .totalPool(BigDecimal.ZERO)
                .build();
        
        assertThrows(CreditLimitException.class, limit::validateCreditLimit);
    }
}
