package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vix.local.api.modules.capital_source.domain.model.PartnerDocument;
import vix.local.api.modules.capital_source.domain.repository.PartnerDocumentRepository;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.document.application.port.StoragePort;
import vix.local.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/documents")
@RequiredArgsConstructor
@Tag(name = "Partner Documents", description = "Quản lý tài liệu của đối tác")
public class PartnerDocumentController {

    private final PartnerDocumentRepository partnerDocumentRepository;
    private final PartnerRepository partnerRepository;
    private final StoragePort storagePort;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Tải lên tài liệu đối tác")
    public ResponseEntity<ApiResponse<PartnerDocument>> uploadDocument(
            @PathVariable UUID partnerId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) {

        if (partnerRepository.findById(partnerId) == null) {
            throw new RuntimeException("Không tìm thấy đối tác");
        }
        
        if (file.isEmpty()) {
            throw new RuntimeException("File không được trống");
        }

        String path = "partners/" + partnerId + "/documents/" + file.getOriginalFilename();
        String storedPath = storagePort.store(file, path);

        PartnerDocument document = PartnerDocument.builder()
                .partnerId(partnerId)
                .name(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .size(file.getSize())
                .storagePath(storedPath)
                .uploadedBy(auth != null ? auth.getName() : "system")
                .createdAt(LocalDateTime.now())
                .build();

        PartnerDocument saved = partnerDocumentRepository.save(document);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tài liệu đối tác")
    public ResponseEntity<ApiResponse<List<PartnerDocument>>> getDocuments(@PathVariable UUID partnerId) {
        List<PartnerDocument> documents = partnerDocumentRepository.findByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/{id}/url")
    @Operation(summary = "Lấy URL tải xuống file")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(
            @PathVariable UUID partnerId,
            @PathVariable UUID id) {
        PartnerDocument document = partnerDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
        
        String url = storagePort.getUrl(document.getStoragePath());
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tài liệu")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable UUID partnerId,
            @PathVariable UUID id) {
        PartnerDocument document = partnerDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
        
        storagePort.delete(document.getStoragePath());
        partnerDocumentRepository.deleteById(id);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
