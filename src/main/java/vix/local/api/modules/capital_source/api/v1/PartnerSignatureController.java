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

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm chữ ký/mẫu dấu cho đối tác")
    public ResponseEntity<ApiResponse<PartnerSignature>> createSignature(
            @PathVariable UUID partnerId,
            @RequestBody PartnerSignature signature,
            Authentication auth) {
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSignature created = partnerService.createSignature(partnerId, signature, updaterId);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách chữ ký/mẫu dấu của đối tác")
    public ResponseEntity<ApiResponse<PagedResponse<PartnerSignature>>> getSignatures(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PartnerSignature> signaturePage = partnerService.getSignaturesByPartnerId(partnerId, pageable);

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
        return ResponseEntity.ok(ApiResponse.success(updated));
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
