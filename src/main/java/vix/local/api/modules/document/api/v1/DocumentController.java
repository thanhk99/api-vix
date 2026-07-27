package vix.local.api.modules.document.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vix.local.api.modules.document.api.v1.dto.response.DocumentResponse;
import vix.local.api.modules.document.application.service.DocumentApplicationService;
import vix.local.api.modules.document.domain.model.Document;
import vix.local.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Lưu trữ và lấy tài liệu bằng MinIO")
public class DocumentController {

    private final DocumentApplicationService documentService;

    // Need to extract companyId and departmentId from the current user context
    // This could be done via AuthPort or from JWT directly if available in
    // SecurityContext
    // We assume the JWT contains companyId and departmentId for now.
    // However, SecurityContextHolder principal usually just holds email.
    // It's better to pass it in request or get it from token correctly.
    // For this example, we require companyId and departmentId from headers.

    @PostMapping("/upload")
    @Operation(summary = "Tải lên tài liệu", description = "Upload file lên MinIO storage cho từng phòng ban")
    public ResponseEntity<ApiResponse<DocumentResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestHeader("X-Department-Id") UUID departmentId,
            Authentication auth) {

        String uploadedBy = auth.getName();
        Document doc = documentService.uploadDocument(file, companyId, departmentId, uploadedBy);

        DocumentResponse response = toResponse(doc);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tài liệu", description = "Lấy danh sách các tài liệu thuộc về 1 phòng ban")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocuments(
            @RequestHeader("X-Department-Id") UUID departmentId) {
        List<DocumentResponse> documents = documentService.getDocumentsByDepartment(departmentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/{id}/url")
    @Operation(summary = "Lấy URL tải xuống file", description = "Tạo Pre-signed URL cho file (có giới hạn thời gian 1 giờ)")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(@PathVariable UUID id) {
        String url = documentService.getDownloadUrl(id);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tài liệu", description = "Xóa file khỏi database và MinIO storage")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .mimeType(document.getMimeType())
                .size(document.getSize())
                // .url(documentService.getDownloadUrl(document.getId())) // Avoid calling
                // external service in mapper, client requests URL explicitly
                .companyId(document.getCompanyId())
                .departmentId(document.getDepartmentId())
                .uploadedBy(document.getUploadedBy())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
