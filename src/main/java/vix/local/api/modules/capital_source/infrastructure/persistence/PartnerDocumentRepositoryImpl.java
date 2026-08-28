package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.PartnerDocument;
import vix.local.api.modules.capital_source.domain.repository.PartnerDocumentRepository;
import vix.local.api.modules.capital_source.infrastructure.entity.PartnerDocumentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PartnerDocumentRepositoryImpl implements PartnerDocumentRepository {

    private final PartnerDocumentJpaRepository jpaRepository;

    @Override
    public List<PartnerDocument> findByPartnerId(UUID partnerId) {
        return jpaRepository.findByPartnerIdOrderByCreatedAtDesc(partnerId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PartnerDocument> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public PartnerDocument save(PartnerDocument document) {
        PartnerDocumentEntity entity = toEntity(document);
        return toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PartnerDocument toModel(PartnerDocumentEntity entity) {
        return PartnerDocument.builder()
                .id(entity.getId())
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .mimeType(entity.getMimeType())
                .size(entity.getSize())
                .storagePath(entity.getStoragePath())
                .uploadedBy(entity.getUploadedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PartnerDocumentEntity toEntity(PartnerDocument model) {
        return PartnerDocumentEntity.builder()
                .id(model.getId())
                .partnerId(model.getPartnerId())
                .name(model.getName())
                .mimeType(model.getMimeType())
                .size(model.getSize())
                .storagePath(model.getStoragePath())
                .uploadedBy(model.getUploadedBy())
                .createdAt(model.getCreatedAt())
                .build();
    }
}
