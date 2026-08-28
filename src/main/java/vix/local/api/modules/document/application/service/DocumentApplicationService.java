package vix.local.api.modules.document.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vix.local.api.modules.document.domain.exception.DocumentException;
import vix.local.api.modules.document.domain.model.Document;
import vix.local.api.modules.document.domain.repository.DocumentRepository;
import vix.local.api.modules.document.application.port.DocumentPort;
import vix.local.api.modules.document.application.port.StoragePort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentApplicationService implements DocumentPort {

    private final DocumentRepository documentRepository;
    private final StoragePort storagePort;

    @Override
    @Transactional
    public Document upload(MultipartFile file, UUID companyId, UUID departmentId, String uploadedBy) {
        if (file.isEmpty()) {
            throw DocumentException.badRequest("File không được trống");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unnamed_file";
        }

        // Generate unique path: companyId/departmentId/uuid_filename
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String storagePath = companyId.toString() + "/" + departmentId.toString() + "/" + uniqueFileName;

        storagePort.store(file, storagePath);

        Document document = Document.builder()
                .name(originalFilename)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .storagePath(storagePath)
                .companyId(companyId)
                .departmentId(departmentId)
                .uploadedBy(uploadedBy)
                .build();

        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByDepartment(UUID departmentId) {
        return documentRepository.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public Document getDocumentById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> DocumentException.notFound("Không tìm thấy tài liệu"));
    }

    @Override
    public String getDownloadUrl(UUID id) {
        Document document = getDocumentById(id);
        return storagePort.getUrl(document.getStoragePath());
    }

    @Override
    public String getPublicUrl(UUID id) {
        Document document = getDocumentById(id);
        return storagePort.getPublicUrl(document.getStoragePath());
    }

    @Override
    public java.io.InputStream loadDocument(UUID id) {
        Document document = getDocumentById(id);
        return storagePort.load(document.getStoragePath());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Document document = getDocumentById(id);
        storagePort.delete(document.getStoragePath());
        documentRepository.deleteById(id);
    }
}
