package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.AssetTransaction;
import vix.local.api.modules.capital_source.domain.repository.AssetTransactionRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.AssetTransactionEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetTransactionRepositoryImpl implements AssetTransactionRepository {

    private final AssetTransactionJpaRepository jpaRepository;

    @Override
    public AssetTransaction save(AssetTransaction transaction) {
        AssetTransactionEntity entity = convertToEntity(transaction);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<AssetTransaction> findById(Long id) {
        return jpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public Page<AssetTransaction> findByFilters(String transType, String counterpartyId, String assetId, String status, Pageable pageable) {
        return jpaRepository.findByFilters(transType, counterpartyId, assetId, status, pageable)
                .map(this::convertToModel);
    }

    @Override
    public boolean existsByAssetId(String assetId) {
        return jpaRepository.existsByAssetId(assetId);
    }

    private AssetTransactionEntity convertToEntity(AssetTransaction model) {
        if (model == null) return null;
        return AssetTransactionEntity.builder()
                .id(model.getId())
                .transType(model.getTransType())
                .counterpartyId(model.getCounterpartyId())
                .assetId(model.getAssetId())
                .tradeDate(model.getTradeDate())
                .settlementDate(model.getSettlementDate())
                .quantity(model.getQuantity())
                .price(model.getPrice())
                .tradeAmount(model.getTradeAmount())
                .feeAmount(model.getFeeAmount())
                .currency(model.getCurrency())
                .referenceNo(model.getReferenceNo())
                .fileUrl(model.getFileUrl())
                .note(model.getNote())
                .status(model.getStatus())
                .rejectReason(model.getRejectReason())
                .createdBy(model.getCreatedBy())
                .approvedBy(model.getApprovedBy())
                .build();
    }

    private AssetTransaction convertToModel(AssetTransactionEntity entity) {
        if (entity == null) return null;
        return AssetTransaction.builder()
                .id(entity.getId())
                .transType(entity.getTransType())
                .counterpartyId(entity.getCounterpartyId())
                .assetId(entity.getAssetId())
                .tradeDate(entity.getTradeDate())
                .settlementDate(entity.getSettlementDate())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .tradeAmount(entity.getTradeAmount())
                .feeAmount(entity.getFeeAmount())
                .currency(entity.getCurrency())
                .referenceNo(entity.getReferenceNo())
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
