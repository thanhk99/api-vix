package vix.local.api.modules.capital_source.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vix.local.api.modules.capital_source.domain.exception.KunnException;
import vix.local.api.modules.capital_source.domain.model.*;
import vix.local.api.modules.capital_source.domain.repository.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KunnApplicationServiceTest {

    @Mock
    private KunnRepository kunnRepository;
    @Mock
    private CreditLimitRepository creditLimitRepository;
    @Mock
    private CreditContractRepository contractRepository;
    
    
    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private KunnApplicationService kunnApplicationService;

    private UUID kunnId = UUID.randomUUID();
    private UUID limitId = UUID.randomUUID();
    private UUID contractId = UUID.randomUUID();
    private UUID partnerId = UUID.randomUUID();
    private UUID approverId = UUID.randomUUID();

    private Kunn kunn;
    private CreditLimit limit;
    private CreditContract contract;
        
    @BeforeEach
    void setUp() {
        kunn = Kunn.builder()
                .id(kunnId)
                .limitId(limitId)
                .lnAmt(new BigDecimal("100")) // 100 tá»·
                .status(KunnStatus.PENDING_APPROVAL)
                .build();

        limit = CreditLimit.builder()
                .id(limitId)
                .contractId(contractId)
                .poolType("CLEAN")
                .totalPool(new BigDecimal("300"))
                .usedPool(new BigDecimal("0"))
                .remainPool(new BigDecimal("300"))
                .status(CreditLimit.STATUS_APPROVED)
                .build();

        contract = CreditContract.builder()
                .id(contractId)
                .partnerId(partnerId)
                .totalLimit(new BigDecimal("500"))
                .usedLimit(new BigDecimal("0"))
                .remainLimit(new BigDecimal("500"))
                .status(CreditContract.STATUS_APPROVED)
                .build();

        

        
    }

    @Test
    void testApproveKunn_Success() {
        // Arrange
        when(kunnRepository.findById(kunnId)).thenReturn(Optional.of(kunn));
        when(creditLimitRepository.findById(limitId)).thenReturn(limit);
        when(contractRepository.findById(contractId)).thenReturn(contract);
        Partner partner = Partner.builder().id(partnerId).remainPool(new BigDecimal("2000")).build();
        when(partnerRepository.findById(partnerId)).thenReturn(partner);
        
        
        when(kunnRepository.save(any(Kunn.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        kunnApplicationService.approveKunn(kunnId, approverId);

        // Assert
        assertEquals(new BigDecimal("100"), limit.getUsedPool());
        assertEquals(new BigDecimal("200"), limit.getRemainPool());

        assertEquals(new BigDecimal("100"), contract.getUsedLimit());
        assertEquals(new BigDecimal("400"), contract.getRemainLimit());

                
                        
        assertEquals(KunnStatus.APPROVED, kunn.getStatus());

        verify(creditLimitRepository).save(limit);
        verify(contractRepository).save(contract);
        
        
        verify(kunnRepository).save(kunn);
    }

    @Test
    void testApproveKunn_ThrowsException_WhenContractLimitExceeded() {
        // Há»£p Ä‘á»“ng chá»‰ cÃ²n 50 tá»·, nhÆ°ng kháº¿ Æ°á»›c cáº§n 100 tá»·
        contract.setRemainLimit(new BigDecimal("50"));
        contract.setUsedLimit(new BigDecimal("450"));

        when(kunnRepository.findById(kunnId)).thenReturn(Optional.of(kunn));
        when(creditLimitRepository.findById(limitId)).thenReturn(limit);
        when(contractRepository.findById(contractId)).thenReturn(contract);
        Partner partner = Partner.builder().id(partnerId).remainPool(new BigDecimal("2000")).build();
        when(partnerRepository.findById(partnerId)).thenReturn(partner);
        
        

        KunnException ex = assertThrows(KunnException.class, () -> {
            kunnApplicationService.approveKunn(kunnId, approverId);
        });

        assertEquals("Số tiền giải ngân không được vượt quá hạn mức còn lại tại thời điểm duyệt", ex.getMessage());
    }
}
