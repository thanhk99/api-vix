package vix.local.api.modules.document.application.port;

import org.springframework.web.multipart.MultipartFile;
import vix.local.api.modules.document.domain.model.Document;

import java.io.InputStream;
import java.util.UUID;

public interface DocumentPort {
    Document upload(MultipartFile file, UUID companyId, UUID departmentId, String uploadedBy);
    String getDownloadUrl(UUID documentId);
    String getPublicUrl(UUID documentId);
    void delete(UUID documentId);
    Document getDocumentById(UUID documentId);
    InputStream loadDocument(UUID documentId);
}
