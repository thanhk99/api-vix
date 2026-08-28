package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetRepository {

    private final AssetJpaRepository assetJpaRepository;

    @Override
    public Asset save(Asset asset) {
        AssetEntity entity = convertToEntity(asset);
        AssetEntity saved = assetJpaRepository.save(entity);
        return convertToModel(saved);
    }

    @Override
    public Optional<Asset> findByAssetId(String assetId) {
        return assetJpaRepository.findByAssetId(assetId).map(this::convertToModel);
    }

    @Override
    public boolean existsByAssetId(String assetId) {
        return assetJpaRepository.existsByAssetId(assetId);
    }

    @Override
    public Page<Asset> findByFilters(String assetId, String assetType, String symbol, String status, Pageable pageable) {
        return assetJpaRepository.findByFilters(assetId, assetType, symbol, status, pageable)
                .map(this::convertToModel);
    }

    @Override
    public void deleteById(UUID id) {
        assetJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return assetJpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public long count() {
        return assetJpaRepository.count();
    }

    private AssetEntity convertToEntity(Asset asset) {
        if (asset == null) return null;
        return AssetEntity.builder()
                .id(asset.getId())
                .assetId(asset.getAssetId())
                .assetType(asset.getAssetType())
                .symbol(asset.getSymbol())
                .currency(asset.getCurrency())
                .issuer(asset.getIssuer())
                .issuerCode(asset.getIssuerCode())
                .parValue(asset.getParValue())
                .marketPrice(asset.getMarketPrice())
                .haircutRate(asset.getHaircutRate())
                .totalQuantity(asset.getTotalQuantity())
                .availQuantity(asset.getAvailQuantity())
                .pledgedQuantity(asset.getPledgedQuantity())
                .issueDate(asset.getIssueDate())
                .maturityDate(asset.getMaturityDate())
                .callDate(asset.getCallDate())
                .couponType(asset.getCouponType())
                .couponRate(asset.getCouponRate())
                .interestPayTerm(asset.getInterestPayTerm())
                .note(asset.getNote())
                .status(asset.getStatus())
                .createdBy(asset.getCreatedBy())
                .updatedBy(asset.getUpdatedBy())
                .build();
    }

    private Asset convertToModel(AssetEntity entity) {
        if (entity == null) return null;
        return Asset.builder()
                .id(entity.getId())
                .assetId(entity.getAssetId())
                .assetType(entity.getAssetType())
                .symbol(entity.getSymbol())
                .currency(entity.getCurrency())
                .issuer(entity.getIssuer())
                .issuerCode(entity.getIssuerCode())
                .parValue(entity.getParValue())
                .marketPrice(entity.getMarketPrice())
                .haircutRate(entity.getHaircutRate())
                .totalQuantity(entity.getTotalQuantity())
                .availQuantity(entity.getAvailQuantity())
                .pledgedQuantity(entity.getPledgedQuantity())
                .issueDate(entity.getIssueDate())
                .maturityDate(entity.getMaturityDate())
                .callDate(entity.getCallDate())
                .couponType(entity.getCouponType())
                .couponRate(entity.getCouponRate())
                .interestPayTerm(entity.getInterestPayTerm())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}