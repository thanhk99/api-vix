package vix.local.api.modules.document.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.document.domain.model.Document;
import vix.local.api.modules.document.domain.repository.DocumentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository jpaRepository;

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Document> findByDepartmentId(UUID departmentId) {
        return jpaRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Document save(Document document) {
        DocumentEntity entity = toEntity(document);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Document toDomain(DocumentEntity entity) {
        if (entity == null) return null;
        return Document.builder()
                .id(entity.getId())
                .name(entity.getName())
                .mimeType(entity.getMimeType())
                .size(entity.getSize())
                .storagePath(entity.getStoragePath())
                .companyId(entity.getCompanyId())
                .departmentId(entity.getDepartmentId())
                .uploadedBy(entity.getUploadedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private DocumentEntity toEntity(Document domain) {
        if (domain == null) return null;
        return DocumentEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .mimeType(domain.getMimeType())
                .size(domain.getSize())
                .storagePath(domain.getStoragePath())
                .companyId(domain.getCompanyId())
                .departmentId(domain.getDepartmentId())
                .uploadedBy(domain.getUploadedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
