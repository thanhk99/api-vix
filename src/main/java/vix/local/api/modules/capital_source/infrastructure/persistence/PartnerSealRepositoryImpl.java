package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.domain.model.PartnerSeal;
import vix.local.api.modules.capital_source.domain.repository.PartnerSealRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerSealEntity;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartnerSealRepositoryImpl implements PartnerSealRepository {
    private final PartnerSealJpaRepository jpaRepository;

    @Override
    public PartnerSeal save(PartnerSeal partnerSeal) {
        PartnerSealEntity entity = convertToEntity(partnerSeal);
        return convertToModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<PartnerSeal> findById(UUID id) {
        return jpaRepository.findById(id).map(this::convertToModel);
    }

    @Override
    public Page<PartnerSeal> findByPartnerId(UUID partnerId, Pageable pageable) {
        return jpaRepository.findByPartnerIdAndStatusNot(partnerId, "DELETED", pageable).map(this::convertToModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerSealEntity convertToEntity(PartnerSeal model) {
        if (model == null) return null;
        PartnerSealEntity entity = new PartnerSealEntity();
        entity.setId(model.getId());
        entity.setPartnerId(model.getPartnerId());
        entity.setSealFileName(model.getSealFileName());
        entity.setDescription(model.getDescription());
        entity.setEffectiveDate(model.getEffectiveDate());
        entity.setExpiryDate(model.getExpiryDate());
        entity.setStatus(model.getStatus());
        entity.setUpdatedBy(model.getUpdatedBy());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    private PartnerSeal convertToModel(PartnerSealEntity entity) {
        if (entity == null) return null;
        return PartnerSeal.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .sealFileName(entity.getSealFileName())
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