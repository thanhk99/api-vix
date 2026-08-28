package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Kunn;
import vix.local.api.modules.capital_source.domain.repository.KunnRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class KunnRepositoryImpl implements KunnRepository {

    private final KunnJpaRepository kunnJpaRepository;

    @Override
    public Kunn save(Kunn kunn) {
        KunnEntity entity = convertToEntity(kunn);
        KunnEntity saved = kunnJpaRepository.save(entity);
        return convertToModel(saved);
    }

    @Override
    public Optional<Kunn> findById(UUID id) {
        return kunnJpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public org.springframework.data.domain.Page<Kunn> findAll(org.springframework.data.domain.Pageable pageable) {
        return kunnJpaRepository.findActiveKunns(pageable)
                .map(this::convertToModel);
    }

    @Override
    public List<Kunn> findAll() {
        return kunnJpaRepository.findAll().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Kunn> findByLimitId(UUID limitId) {
        return kunnJpaRepository.findByLimitId(limitId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Kunn> findByCusId(UUID cusId) {
        return kunnJpaRepository.findByCusId(cusId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Kunn> saveAll(List<Kunn> kunns) {
        List<KunnEntity> entities = kunns.stream().map(this::convertToEntity).collect(Collectors.toList());
        return kunnJpaRepository.saveAll(entities).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public java.math.BigDecimal sumPendingLnAmtByLimitId(UUID limitId, UUID excludeKunnId) {
        return kunnJpaRepository.sumPendingLnAmtByLimitId(limitId, excludeKunnId);
    }

    @Override
    public java.math.BigDecimal sumPendingLnAmtByContractId(UUID contractId, UUID excludeKunnId) {
        return kunnJpaRepository.sumPendingLnAmtByContractId(contractId, excludeKunnId);
    }

    @Override
    public java.math.BigDecimal sumPendingLnAmtByPartnerId(UUID partnerId, UUID excludeKunnId) {
        return kunnJpaRepository.sumPendingLnAmtByPartnerId(partnerId, excludeKunnId);
    }

    private KunnEntity convertToEntity(Kunn kunn) {
        if (kunn == null) return null;
        return KunnEntity.builder()
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

    private Kunn convertToModel(KunnEntity entity) {
        if (entity == null) return null;
        return Kunn.builder()
                .id(entity.getId())
                .cusId(entity.getCusId())
                .contactNo(entity.getContactNo())
                .limitId(entity.getLimitId())
                .lnContactNo(entity.getLnContactNo())
                .lnContactDate(entity.getLnContactDate())
                .lnAmt(entity.getLnAmt())
                .lnDate(entity.getLnDate())
                .contractIntRate(entity.getContractIntRate())
                .actIntRate(entity.getActIntRate())
                .reason(entity.getReason())
                .casaRate(entity.getCasaRate())
                .settDate(entity.getSettDate())
                .term(entity.getTerm())
                .currency(entity.getCurrency())
                .purpose(entity.getPurpose())
                .intTerm(entity.getIntTerm())
                .prinTerm(entity.getPrinTerm())
                .prepaymentNote(entity.getPrepaymentNote())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createUser(entity.getCreateUser())
                .approveDate(entity.getApproveDate())
                .approveUser(entity.getApproveUser())
                .build();
    }
}
