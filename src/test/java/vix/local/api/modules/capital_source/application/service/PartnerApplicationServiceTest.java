package vix.local.api.modules.capital_source.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vix.local.api.modules.capital_source.api.v1.dto.request.GlobalCreditLimitRequestDto;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import vix.local.api.modules.capital_source.domain.model.CreditContract;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.CreditContractRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerApplicationServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private CreditContractRepository contractRepository;

    @Mock
    private CreditLimitRepository creditLimitRepository;

    @InjectMocks
    private PartnerApplicationService partnerApplicationService;

    private Partner partner;

    @BeforeEach
    void setUp() {
        partner = Partner.builder()
                .id(UUID.randomUUID())
                .status(Partner.STATUS_APPROVED)
                .build();
    }

    @Test
    void createGlobalCreditLimit_shouldCreateWithNewContract_whenContractIdIsNull() {
        GlobalCreditLimitRequestDto request = new GlobalCreditLimitRequestDto();
        request.setPartnerId(partner.getId());
        request.setContractNo("CONTRACT_NEW");
        request.setContractType("CREDIT_LIMIT");
        request.setContractTotalLimit(new BigDecimal("5000"));
        request.setPoolType("NORMAL");
        request.setTotalPool(new BigDecimal("1000"));
        request.setStartDate(LocalDate.now());

        when(partnerRepository.findById(partner.getId())).thenReturn(partner);
        
        when(contractRepository.save(any(CreditContract.class))).thenAnswer(invocation -> {
            CreditContract contract = invocation.getArgument(0);
            contract.setId(UUID.randomUUID());
            return contract;
        });

        when(creditLimitRepository.save(any(CreditLimit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditLimit created = partnerApplicationService.createGlobalCreditLimit(request);

        assertNotNull(created);
        assertNotNull(created.getContractId());
        assertEquals("CONTRACT_NEW_NORMAL", created.getLimitId());
        assertEquals(new BigDecimal("1000"), created.getRemainPool());
        
        verify(contractRepository).save(any(CreditContract.class));
        verify(creditLimitRepository).save(any(CreditLimit.class));
    }

    @Test
    void createGlobalCreditLimit_shouldCreateWithExistingContract_whenContractIdIsProvided() {
        UUID existingContractId = UUID.randomUUID();
        GlobalCreditLimitRequestDto request = new GlobalCreditLimitRequestDto();
        request.setPartnerId(partner.getId());
        request.setContractId(existingContractId);
        request.setPoolType("MARGIN");
        request.setPurpose("Purpose 1");
        request.setTotalPool(new BigDecimal("2000"));
        request.setStartDate(LocalDate.now());

        CreditContract existingContract = CreditContract.builder()
                .id(existingContractId)
                .contractNo("CONTRACT_OLD")
                .build();

        when(partnerRepository.findById(partner.getId())).thenReturn(partner);
        when(contractRepository.findById(existingContractId)).thenReturn(existingContract);
        when(creditLimitRepository.save(any(CreditLimit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditLimit created = partnerApplicationService.createGlobalCreditLimit(request);

        assertNotNull(created);
        assertEquals(existingContractId, created.getContractId());
        assertEquals("CONTRACT_OLD_MARGIN", created.getLimitId());
        assertEquals(new BigDecimal("2000"), created.getRemainPool());
        
        verify(contractRepository, never()).save(any(CreditContract.class));
        verify(creditLimitRepository).save(any(CreditLimit.class));
    }

    @Test
    void createGlobalCreditLimit_shouldThrow_whenPartnerNotFound() {
        GlobalCreditLimitRequestDto request = new GlobalCreditLimitRequestDto();
        request.setPartnerId(UUID.randomUUID());

        when(partnerRepository.findById(request.getPartnerId())).thenReturn(null);

        assertThrows(PartnerException.class, () -> partnerApplicationService.createGlobalCreditLimit(request));
    }
}
