package vix.local.api.modules.document.domain.repository;

import vix.local.api.modules.document.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Optional<Document> findById(UUID id);
    List<Document> findByDepartmentId(UUID departmentId);
    Document save(Document document);
    void deleteById(UUID id);
}
