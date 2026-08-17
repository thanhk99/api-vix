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
    public List<Kunn> findAll() {
        return kunnJpaRepository.findAll().stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
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
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createUser(entity.getCreateUser())
                .approveDate(entity.getApproveDate())
                .approveUser(entity.getApproveUser())
                .build();
    }
}
