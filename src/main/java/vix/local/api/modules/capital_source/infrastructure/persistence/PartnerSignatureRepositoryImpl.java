package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.PartnerSignature;
import vix.local.api.modules.capital_source.domain.repository.PartnerSignatureRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerSignatureEntity;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartnerSignatureRepositoryImpl implements PartnerSignatureRepository {

    private final PartnerSignatureJpaRepository jpaRepository;

    @Override
    public PartnerSignature save(PartnerSignature signature) {
        PartnerSignatureEntity entity = convertToEntity(signature);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public PartnerSignature findById(UUID id) {
        return jpaRepository.findById(id).map(this::convertToModel).orElse(null);
    }

    @Override
    public Page<PartnerSignature> findByPartnerId(UUID partnerId, Pageable pageable) {
        return jpaRepository.findByPartnerId(partnerId, pageable)
                .map(this::convertToModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerSignatureEntity convertToEntity(PartnerSignature model) {
        if (model == null) return null;
        PartnerSignatureEntity entity = new PartnerSignatureEntity();
        entity.setId(model.getId());
        entity.setPartnerId(model.getPartnerId());
        entity.setDocumentId(model.getDocumentId());
        entity.setSignFileName(model.getSignFileName());
        entity.setSignType(model.getSignType());
        entity.setDescription(model.getDescription());
        entity.setEffectiveDate(model.getEffectiveDate());
        entity.setExpiryDate(model.getExpiryDate());
        entity.setStatus(model.getStatus());
        entity.setUpdatedBy(model.getUpdatedBy());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    private PartnerSignature convertToModel(PartnerSignatureEntity entity) {
        if (entity == null) return null;
        return PartnerSignature.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .documentId(entity.getDocumentId())
                .signFileName(entity.getSignFileName())
                .signType(entity.getSignType())
                .description(entity.getDescription())
                .effectiveDate(entity.getEffectiveDate())
                .expiryDate(entity.getExpiryDate())
                .status(entity.getStatus())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
