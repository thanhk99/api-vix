package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.api.v1.dto.request.KunnRequestDto;
import vix.local.api.modules.capital_source.api.v1.dto.response.KunnResponseDto;
import vix.local.api.modules.capital_source.domain.exception.KunnException;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.modules.capital_source.domain.model.Kunn;
import vix.local.api.modules.capital_source.domain.repository.CreditLimitRepository;
import vix.local.api.modules.capital_source.domain.repository.KunnRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
// Optional: import user service or repository if we need to resolve user names. For now, passing names as null or using a dummy map.

@Service
@RequiredArgsConstructor
public class KunnApplicationService {

    private final KunnRepository kunnRepository;
    private final CreditLimitRepository creditLimitRepository;

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
                .createUser(userId)
                .build();

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

        kunn.validateUpdate(limit.getRemainPool());

        Kunn updated = kunnRepository.save(kunn);
        return mapToResponse(updated);
    }

    @Transactional
    public KunnResponseDto approveKunn(UUID id, UUID approverId) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        CreditLimit limit = creditLimitRepository.findById(kunn.getLimitId());
        if (limit == null) {
            throw KunnException.notFound("Không tìm thấy Hạn mức với ID: " + kunn.getLimitId());
        }

        kunn.approve(approverId, limit.getRemainPool());

        Kunn approved = kunnRepository.save(kunn);
        return mapToResponse(approved);
    }

    @Transactional
    public KunnResponseDto cancelKunn(UUID id) {
        Kunn kunn = kunnRepository.findById(id)
                .orElseThrow(() -> KunnException.notFound("Không tìm thấy KUNN với ID: " + id));

        kunn.cancel();

        Kunn canceled = kunnRepository.save(kunn);
        return mapToResponse(canceled);
    }

    @Transactional(readOnly = true)
    public List<KunnResponseDto> getAllKunns() {
        return kunnRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private KunnResponseDto mapToResponse(Kunn kunn) {
        return KunnResponseDto.builder()
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
                .status(kunn.getStatus())
                .createdDate(kunn.getCreatedDate())
                .createUser(kunn.getCreateUser())
                .approveDate(kunn.getApproveDate())
                .approveUser(kunn.getApproveUser())
                .build();
    }
}
