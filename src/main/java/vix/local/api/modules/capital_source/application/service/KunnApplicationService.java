package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.api.v1.dto.request.KunnRequestDto;
import vix.local.api.modules.capital_source.api.v1.dto.response.KunnResponseDto;
import vix.local.api.modules.capital_source.domain.exception.KunnException;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Kunn;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.KunnRepository;
import vix.local.api.modules.capital_source.domain.repository.CreditContractRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KunnApplicationService {

    private final KunnRepository kunnRepository;
    private final CreditLimitRepository creditLimitRepository;
    private final CreditContractRepository contractRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public KunnResponseDto createKunn(KunnRequestDto requestDto, UUID userId) {
        CreditLimit limit = creditLimitRepository.findById(requestDto.getLimitId());
        if (limit == null) {
            throw KunnException.notFound("Không tìm thấy Hạn mức với ID: " + requestDto.getLimitId());
        }

        Kunn kunn = Kunn.builder()
                .cusId(requestDto.getCusId())
                .contactNo(requestDto.getContactNo())
                .limitId(requestDto.getLimitId())
                .lnContactNo(requestDto.getLnContactNo())
                .lnContactDate(requestDto.getLnContactDate())
                .lnAmt(requestDto.getLnAmt())
                .lnDate(requestDto.getLnDate())
                .contractIntRate(requestDto.getContractIntRate())
                .actIntRate(requestDto.getActIntRate())
                .reason(requestDto.getReason())
                .casaRate(requestDto.getCasaRate())
                .settDate(requestDto.getSettDate())
                .term(requestDto.getTerm())
                .currency(requestDto.getCurrency())
                .purpose(requestDto.getPurpose())
                .intTerm(requestDto.getIntTerm())
                .prinTerm(requestDto.getPrinTerm())
                .prepaymentNote(requestDto.getPrepaymentNote())
                .note(requestDto.getNote())
                .createUser(userId)
                .build();

        validate3TierLimits(kunn, limit);
        kunn.validateCreation(limit.getRemainPool(), limit.getStartDate(), limit.getEndDate());

        Kunn saved = kunnRepository.save(kunn);
        return mapToResponse(saved);
    }

    @Transactional
    public KunnResponseDto updateKunn(UUID id, KunnRequestDto requestDto) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        CreditLimit limit = creditLimitRepository.findById(requestDto.getLimitId());
        if (limit == null) {
            throw KunnException.notFound("Không tìm thấy Hạn mức với ID: " + requestDto.getLimitId());
        }
        if (!CreditLimit.STATUS_APPROVED.equals(limit.getStatus())) {
            throw KunnException.badRequest("Hạn mức chưa được duyệt hoặc đang chờ xoá, không thể thao tác");
        }
        Partner partner = partnerRepository.findById(limit.getPartnerId());
        if (partner == null || !Partner.STATUS_APPROVED.equals(partner.getStatus())) {
            throw KunnException.badRequest("Đối tác chưa được duyệt hoặc đang chờ xoá, không thể thao tác");
        }


        kunn.setCusId(requestDto.getCusId());
        kunn.setContactNo(requestDto.getContactNo());
        kunn.setLimitId(requestDto.getLimitId());
        kunn.setLnContactNo(requestDto.getLnContactNo());
        kunn.setLnContactDate(requestDto.getLnContactDate());
        kunn.setLnAmt(requestDto.getLnAmt());
        kunn.setLnDate(requestDto.getLnDate());
        kunn.setContractIntRate(requestDto.getContractIntRate());
        kunn.setActIntRate(requestDto.getActIntRate());
        kunn.setReason(requestDto.getReason());
        kunn.setCasaRate(requestDto.getCasaRate());
        kunn.setSettDate(requestDto.getSettDate());
        kunn.setTerm(requestDto.getTerm());
        kunn.setCurrency(requestDto.getCurrency());
        kunn.setPurpose(requestDto.getPurpose());
        kunn.setIntTerm(requestDto.getIntTerm());
        kunn.setPrinTerm(requestDto.getPrinTerm());
        kunn.setPrepaymentNote(requestDto.getPrepaymentNote());
        kunn.setNote(requestDto.getNote());

        validate3TierLimits(kunn, limit);
        kunn.validateUpdate(limit.getRemainPool());

        Kunn updated = kunnRepository.save(kunn);
        return mapToResponse(updated);
    }
    
    private void validate3TierLimits(Kunn kunn, CreditLimit limit) {
        vix.local.api.modules.capital_source.domain.model.CreditContract contract = contractRepository.findById(limit.getContractId());
        Partner contractPartner = contract != null ? partnerRepository.findById(contract.getPartnerId()) : null;
        
        java.math.BigDecimal limitRemain = limit.getRemainPool();
        java.math.BigDecimal contractRemain = contract != null ? contract.getRemainLimit() : null;
        java.math.BigDecimal partnerRemain = contractPartner != null ? contractPartner.getRemainPool() : null;
        
        UUID excludeKunnId = kunn.getId();

        java.math.BigDecimal pendingLimit = kunnRepository.sumPendingLnAmtByLimitId(limit.getId(), excludeKunnId);
        java.math.BigDecimal pendingContract = contract != null ? kunnRepository.sumPendingLnAmtByContractId(contract.getId(), excludeKunnId) : java.math.BigDecimal.ZERO;
        java.math.BigDecimal pendingPartner = contractPartner != null ? kunnRepository.sumPendingLnAmtByPartnerId(contractPartner.getId(), excludeKunnId) : java.math.BigDecimal.ZERO;

        java.math.BigDecimal limitAvailable = limitRemain != null ? limitRemain.subtract(pendingLimit) : null;
        java.math.BigDecimal contractAvailable = contractRemain != null ? contractRemain.subtract(pendingContract) : null;
        java.math.BigDecimal partnerAvailable = partnerRemain != null ? partnerRemain.subtract(pendingPartner) : null;
        
        if (partnerAvailable != null && kunn.getLnAmt().compareTo(partnerAvailable) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân vượt quá hạn mức Khả dụng của Đối tác (Còn lại: " + partnerAvailable + ")");
        }
        if (contractAvailable != null && kunn.getLnAmt().compareTo(contractAvailable) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân vượt quá hạn mức Khả dụng của Hợp đồng (Còn lại: " + contractAvailable + ")");
        }
        if (limitAvailable != null && kunn.getLnAmt().compareTo(limitAvailable) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân vượt quá hạn mức Khả dụng của Tiểu mục (Còn lại: " + limitAvailable + ")");
        }
    }

    @Transactional
    public KunnResponseDto approveKunn(UUID id, UUID approverId) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        CreditLimit limit = creditLimitRepository.findById(kunn.getLimitId());
        if (limit == null) {
            throw KunnException.notFound("Không tìm thấy Hạn mức với ID: " + kunn.getLimitId());
        }
        vix.local.api.modules.capital_source.domain.model.CreditContract contract = contractRepository.findById(limit.getContractId());
        Partner contractPartner = contract != null ? partnerRepository.findById(contract.getPartnerId()) : null;

        java.math.BigDecimal limitRemain = limit.getRemainPool();
        java.math.BigDecimal contractRemain = contract != null ? contract.getRemainLimit() : null;
        java.math.BigDecimal partnerRemain = contractPartner != null ? contractPartner.getRemainPool() : null;
        
        java.math.BigDecimal minRemain = limitRemain;
        if (contractRemain != null && contractRemain.compareTo(minRemain) < 0) minRemain = contractRemain;
        if (partnerRemain != null && partnerRemain.compareTo(minRemain) < 0) minRemain = partnerRemain;

        kunn.approve(approverId, minRemain);
        
        limit.consume(kunn.getLnAmt());
        if (contract != null) {
            contract.consume(kunn.getLnAmt());
            contractRepository.save(contract);
        }
        if (contractPartner != null) {
            contractPartner.consume(kunn.getLnAmt());
            partnerRepository.save(contractPartner);
        }
        creditLimitRepository.save(limit);

        Kunn approved = kunnRepository.save(kunn);
        return mapToResponse(approved);
    }

    @Transactional(readOnly = true)
    public KunnResponseDto getKunnById(UUID id) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));
        if (vix.local.api.modules.capital_source.domain.model.KunnStatus.DELETED.equals(kunn.getStatus())) {
            throw KunnException.notFound("Không tìm thấy KUNN với ID: " + id);
        }
        return mapToResponse(kunn);
    }

    @Transactional
    public KunnResponseDto cancelKunn(UUID id) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        kunn.requestCancel();

        Kunn canceled = kunnRepository.save(kunn);
        return mapToResponse(canceled);
    }

    @Transactional
    public KunnResponseDto approveCancelKunn(UUID id) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        kunn.approveCancel();
        
        CreditLimit limit = creditLimitRepository.findById(kunn.getLimitId());
        if (limit != null) {
            limit.release(kunn.getLnAmt());
            vix.local.api.modules.capital_source.domain.model.CreditContract contract = contractRepository.findById(limit.getContractId());
            Partner contractPartner = contract != null ? partnerRepository.findById(contract.getPartnerId()) : null;
            if (contract != null) {
                contract.release(kunn.getLnAmt());
                contractRepository.save(contract);
            }
            if (contractPartner != null) {
                contractPartner.release(kunn.getLnAmt());
                partnerRepository.save(contractPartner);
            }
            creditLimitRepository.save(limit);
        }

        return mapToResponse(kunnRepository.save(kunn));
    }

    @Transactional
    public KunnResponseDto rejectCancelKunn(UUID id) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        kunn.rejectCancel();
        return mapToResponse(kunnRepository.save(kunn));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<KunnResponseDto> getAllKunns(org.springframework.data.domain.Pageable pageable) {
        return kunnRepository.findAll(pageable).map(this::mapToResponse);
    }

    private KunnResponseDto mapToResponse(Kunn kunn) {
        Partner partner = partnerRepository.findById(kunn.getCusId());
        String cusName = partner != null ? partner.getCusName() : null;
        String cusIdCode = partner != null ? partner.getIdCode() : null;
        return KunnResponseDto.builder()
                .cusName(cusName)
                .cusIdCode(cusIdCode)
                .id(kunn.getId())
                .cusId(kunn.getCusId())
                .contactNo(kunn.getContactNo())
                .limitId(kunn.getLimitId())
                .lnContactNo(kunn.getLnContactNo())
                .lnContactDate(kunn.getLnContactDate())
                .lnAmt(kunn.getLnAmt())
                .lnDate(kunn.getLnDate())
                .contractIntRate(kunn.getContractIntRate())
                .actIntRate(kunn.getActIntRate())
                .reason(kunn.getReason())
                .casaRate(kunn.getCasaRate())
                .settDate(kunn.getSettDate())
                .term(kunn.getTerm())
                .currency(kunn.getCurrency())
                .purpose(kunn.getPurpose())
                .intTerm(kunn.getIntTerm())
                .prinTerm(kunn.getPrinTerm())
                .prepaymentNote(kunn.getPrepaymentNote())
                .note(kunn.getNote())
                .status(kunn.getStatus())
                .createdDate(kunn.getCreatedDate())
                .createUser(kunn.getCreateUser())
                .approveDate(kunn.getApproveDate())
                .approveUser(kunn.getApproveUser())
                .build();
    }
}
