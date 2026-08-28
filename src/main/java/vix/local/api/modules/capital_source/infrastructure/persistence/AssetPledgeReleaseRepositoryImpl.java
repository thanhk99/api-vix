package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.AssetPledgeRelease;
import vix.local.api.modules.capital_source.domain.repository.AssetPledgeReleaseRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetPledgeReleaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AssetPledgeReleaseRepositoryImpl implements AssetPledgeReleaseRepository {

    private final AssetPledgeReleaseJpaRepository jpaRepository;

    @Override
    public AssetPledgeRelease save(AssetPledgeRelease release) {
        AssetPledgeReleaseEntity entity = convertToEntity(release);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<AssetPledgeRelease> findById(Long id) {
        return jpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public List<AssetPledgeRelease> findByPledgeId(Long pledgeId) {
        return jpaRepository.findByPledgeId(pledgeId).stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AssetPledgeRelease> findByPledgeId(Long pledgeId, Pageable pageable) {
        return jpaRepository.findByPledgeId(pledgeId, pageable).map(this::convertToModel);
    }

    @Override
    public Page<AssetPledgeRelease> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::convertToModel);
    }

    private AssetPledgeReleaseEntity convertToEntity(AssetPledgeRelease model) {
        if (model == null) return null;
        return AssetPledgeReleaseEntity.builder()
                .id(model.getId())
                .pledgeId(model.getPledgeId())
                .releaseQty(model.getReleaseQty())
                .releaseValue(model.getReleaseValue())
                .releaseDate(model.getReleaseDate())
                .reason(model.getReason())
                .note(model.getNote())
                .fileUrl(model.getFileUrl())
                .isExceptionApproved(model.getIsExceptionApproved())
                .exceptionApprover(model.getExceptionApprover())
                .exceptionReason(model.getExceptionReason())
                .status(model.getStatus())
                .rejectReason(model.getRejectReason())
                .createdBy(model.getCreatedBy())
                .approvedBy(model.getApprovedBy())
                .build();
    }

    private AssetPledgeRelease convertToModel(AssetPledgeReleaseEntity entity) {
        if (entity == null) return null;
        return AssetPledgeRelease.builder()
                .id(entity.getId())
                .pledgeId(entity.getPledgeId())
                .releaseQty(entity.getReleaseQty())
                .releaseValue(entity.getReleaseValue())
                .releaseDate(entity.getReleaseDate())
                .reason(entity.getReason())
                .note(entity.getNote())
                .fileUrl(entity.getFileUrl())
                .isExceptionApproved(entity.getIsExceptionApproved())
                .exceptionApprover(entity.getExceptionApprover())
                .exceptionReason(entity.getExceptionReason())
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .createdBy(entity.getCreatedBy())
                .approvedBy(entity.getApprovedBy())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}
