package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.PartnerSignature;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/signatures")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class PartnerSignatureController {

    private final PartnerApplicationService partnerService;
    private final UserRepository userRepository;

    private UUID getUserIdFromAuth(Authentication auth) {
        if (auth != null && auth.getName() != null) {
            return userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        return null;
    }

    private void populateUserNames(java.util.List<PartnerSignature> signatures) {
        if (signatures == null || signatures.isEmpty()) return;
        java.util.Map<UUID, String> cache = new java.util.HashMap<>();
        signatures.forEach(s -> {
            if (s.getUpdatedBy() != null) {
                String name = cache.computeIfAbsent(s.getUpdatedBy(), id -> 
                    userRepository.findById(id).map(u -> 
                        (u.getFullName() != null && !u.getFullName().trim().isEmpty()) 
                            ? u.getFullName() 
                            : (u.getEmail() != null ? u.getEmail() : id.toString())
                    ).orElse(id.toString())
                );
                s.setUpdatedByName(name);
            }
        });
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm chữ ký/mẫu dấu cho đối tác")
    public ResponseEntity<ApiResponse<PartnerSignature>> createSignature(
            @PathVariable UUID partnerId,
            @RequestBody PartnerSignature signature,
            Authentication auth) {
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSignature created = partnerService.createSignature(partnerId, signature, updaterId);
        populateUserNames(java.util.Collections.singletonList(created));
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách chữ ký/mẫu dấu của đối tác")
    public ResponseEntity<ApiResponse<PagedResponse<PartnerSignature>>> getSignatures(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<PartnerSignature> signaturePage = partnerService.getSignaturesByPartnerId(partnerId, pageable);
        populateUserNames(signaturePage.getContent());

        PagedResponse<PartnerSignature> pagedResponse = PagedResponse.<PartnerSignature>builder()
                .content(signaturePage.getContent())
                .pageNumber(signaturePage.getNumber())
                .pageSize(signaturePage.getSize())
                .totalElements(signaturePage.getTotalElements())
                .totalPages(signaturePage.getTotalPages())
                .isLast(signaturePage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{signatureId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật chữ ký/mẫu dấu")
    public ResponseEntity<ApiResponse<PartnerSignature>> updateSignature(
            @PathVariable UUID partnerId,
            @PathVariable UUID signatureId,
            @RequestBody PartnerSignature updateRequest,
            Authentication auth) {
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSignature updated = partnerService.updateSignature(partnerId, signatureId, updateRequest, updaterId);
        populateUserNames(java.util.Collections.singletonList(updated));
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PostMapping(value = "/{signatureId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Upload file chữ ký", description = "Tải file ảnh chữ ký lên hệ thống lưu trữ")
    public ResponseEntity<ApiResponse<PartnerSignature>> uploadSignatureFile(
            @PathVariable UUID partnerId,
            @PathVariable UUID signatureId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-Company-Id", required = false) UUID companyId,
            @RequestHeader(value = "X-Department-Id", required = false) UUID departmentId,
            Authentication auth) {
        
        UUID effectiveCompanyId = companyId != null ? companyId : UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID effectiveDepartmentId = departmentId != null ? departmentId : UUID.fromString("00000000-0000-0000-0000-000000000000");
        
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSignature updated = partnerService.uploadSignatureFile(partnerId, signatureId, file, effectiveCompanyId, effectiveDepartmentId, updaterId);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @GetMapping("/{signatureId}/preview")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy URL xem trước", description = "Trả về Pre-signed URL an toàn để xem ảnh chữ ký")
    public ResponseEntity<ApiResponse<String>> getSignaturePreviewUrl(
            @PathVariable UUID partnerId,
            @PathVariable UUID signatureId) {
        String url = partnerService.getSignaturePreviewUrl(partnerId, signatureId);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @GetMapping("/{signatureId}/download")
    @Operation(summary = "Tải / xem trực tiếp file chữ ký")
    public ResponseEntity<org.springframework.core.io.Resource> downloadSignature(
            @PathVariable UUID partnerId,
            @PathVariable UUID signatureId) {
        vix.local.api.modules.document.domain.model.Document doc = partnerService.getSignatureDocument(partnerId, signatureId);
        org.springframework.core.io.Resource resource = partnerService.getSignatureFileResource(partnerId, signatureId);
        if (doc == null || resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        String mimeType = doc.getMimeType() != null ? doc.getMimeType() : "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{signatureId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xoá chữ ký/mẫu dấu")
    public ResponseEntity<ApiResponse<Void>> deleteSignature(
            @PathVariable UUID partnerId,
            @PathVariable UUID signatureId) {
        partnerService.deleteSignature(partnerId, signatureId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
