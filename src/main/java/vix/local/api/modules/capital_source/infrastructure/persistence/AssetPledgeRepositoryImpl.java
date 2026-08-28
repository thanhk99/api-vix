package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.AssetPledge;
import vix.local.api.modules.capital_source.domain.repository.AssetPledgeRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetPledgeEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetPledgeRepositoryImpl implements AssetPledgeRepository {

    private final AssetPledgeJpaRepository jpaRepository;

    @Override
    public AssetPledge save(AssetPledge pledge) {
        AssetPledgeEntity entity = convertToEntity(pledge);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<AssetPledge> findById(Long id) {
        return jpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public Page<AssetPledge> findByFilters(String cusId, String contractNo, String limitId, String assetId, String status, Pageable pageable) {
        return jpaRepository.findByFilters(cusId, contractNo, limitId, assetId, status, pageable)
                .map(this::convertToModel);
    }

    @Override
    public boolean existsByAssetId(String assetId) {
        return jpaRepository.existsByAssetId(assetId);
    }

    private AssetPledgeEntity convertToEntity(AssetPledge model) {
        if (model == null) return null;
        return AssetPledgeEntity.builder()
                .id(model.getId())
                .assetId(model.getAssetId())
                .cusId(model.getCusId())
                .contractNo(model.getContractNo())
                .limitId(model.getLimitId())
                .pledgePlace(model.getPledgePlace())
                .pledgeDate(model.getPledgeDate())
                .endPledgeDate(model.getEndPledgeDate())
                .pledgeQty(model.getPledgeQty())
                .releasedQty(model.getReleasedQty())
                .price(model.getPrice())
                .marketValue(model.getMarketValue())
                .haircutRate(model.getHaircutRate())
                .collateralValue(model.getCollateralValue())
                .pledgeContractNo(model.getPledgeContractNo())
                .fileUrl(model.getFileUrl())
                .note(model.getNote())
                .status(model.getStatus())
                .rejectReason(model.getRejectReason())
                .createdBy(model.getCreatedBy())
                .approvedBy(model.getApprovedBy())
                .build();
    }

    private AssetPledge convertToModel(AssetPledgeEntity entity) {
        if (entity == null) return null;
        return AssetPledge.builder()
                .id(entity.getId())
                .assetId(entity.getAssetId())
                .cusId(entity.getCusId())
                .contractNo(entity.getContractNo())
                .limitId(entity.getLimitId())
                .pledgePlace(entity.getPledgePlace())
                .pledgeDate(entity.getPledgeDate())
                .endPledgeDate(entity.getEndPledgeDate())
                .pledgeQty(entity.getPledgeQty())
                .releasedQty(entity.getReleasedQty())
                .price(entity.getPrice())
                .marketValue(entity.getMarketValue())
                .haircutRate(entity.getHaircutRate())
                .collateralValue(entity.getCollateralValue())
                .pledgeContractNo(entity.getPledgeContractNo())
                .fileUrl(entity.getFileUrl())
                .note(entity.getNote())
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .createdBy(entity.getCreatedBy())
                .approvedBy(entity.getApprovedBy())
                .createdAt(entity.getCreatedAt())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}
