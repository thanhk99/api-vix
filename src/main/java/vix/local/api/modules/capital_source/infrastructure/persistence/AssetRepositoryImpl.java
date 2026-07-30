package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.modules.capital_source.domain.repository.AssetRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetEntity;
import java.util.List;
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
    public List<Asset> findByPartnerId(UUID partnerId) {
        return assetJpaRepository.findByPartnerId(partnerId).stream()
                .map(this::convertToModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        assetJpaRepository.deleteById(id);
    }

    @Override
    public Asset findById(UUID id) {
        return convertToModel(assetJpaRepository.findById(id).orElse(null));
    }

    private AssetEntity convertToEntity(Asset asset) {
        AssetEntity entity = AssetEntity.builder()
                .id(asset.getId())
                .partnerId(asset.getPartnerId())
                .assetId(asset.getAssetId())
                .assetType(asset.getAssetType())
                .issuer(asset.getIssuer())
                .issuerCode(asset.getIssuerCode())
                .parValue(asset.getParValue())
                .issueDate(asset.getIssueDate())
                .maturityDate(asset.getMaturityDate())
                .callDate(asset.getCallDate())
                .couponType(asset.getCouponType())
                .couponRate(asset.getCouponRate())
                .interestPayTerm(asset.getInterestPayTerm())
                .build();
        return entity;
    }

    private Asset convertToModel(AssetEntity entity) {
        if (entity == null) return null;
        return Asset.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .assetId(entity.getAssetId())
                .assetType(entity.getAssetType())
                .issuer(entity.getIssuer())
                .issuerCode(entity.getIssuerCode())
                .parValue(entity.getParValue())
                .issueDate(entity.getIssueDate())
                .maturityDate(entity.getMaturityDate())
                .callDate(entity.getCallDate())
                .couponType(entity.getCouponType())
                .couponRate(entity.getCouponRate())
                .interestPayTerm(entity.getInterestPayTerm())
                .build();
    }
}