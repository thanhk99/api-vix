package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.PartnerContact;
import vix.local.api.modules.capital_source.domain.repository.PartnerContactRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerContactEntity;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PartnerContactRepositoryImpl implements PartnerContactRepository {

    private final PartnerContactJpaRepository jpaRepository;

    @Override
    public PartnerContact save(PartnerContact contact) {
        return toModel(jpaRepository.save(toEntity(contact)));
    }

    @Override
    public PartnerContact findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel).orElse(null);
    }

    @Override
    public Page<PartnerContact> findByPartnerIdAndStatusNot(UUID partnerId, String status, Pageable pageable) {
        return jpaRepository.findByPartnerIdAndStatusNot(partnerId, status, pageable).map(this::toModel);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerContactEntity toEntity(PartnerContact model) {
        if (model == null) return null;
        PartnerContactEntity entity = new PartnerContactEntity();
        entity.setId(model.getId());
        entity.setPartnerId(model.getPartnerId());
        entity.setName(model.getName());
        entity.setPosition(model.getPosition());
        entity.setDepartment(model.getDepartment());
        entity.setPhone(model.getPhone());
        entity.setEmail(model.getEmail());
        entity.setRole(model.getRole());
        entity.setTransactionFee(model.getTransactionFee());
        entity.setNote(model.getNote());
        entity.setStatus(model.getStatus());
        entity.setCreatedBy(model.getCreatedBy());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedBy(model.getUpdatedBy());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    private PartnerContact toModel(PartnerContactEntity entity) {
        if (entity == null) return null;
        return PartnerContact.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .position(entity.getPosition())
                .department(entity.getDepartment())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .role(entity.getRole())
                .transactionFee(entity.getTransactionFee())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
