package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/authorizations")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class AuthorizationController {

    private final PartnerApplicationService partnerService;

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm người uỷ quyền cho đối tác")
    public ResponseEntity<ApiResponse<Authorization>> createAuthorization(
            @PathVariable UUID partnerId,
            @RequestBody Authorization authorization) {
        Authorization created = partnerService.createAuthorization(partnerId, authorization);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách người uỷ quyền của đối tác")
    public ResponseEntity<ApiResponse<PagedResponse<Authorization>>> getAuthorizations(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<Authorization> authPage = partnerService.getAuthorizationsByPartnerId(partnerId, pageable);

        PagedResponse<Authorization> pagedResponse = PagedResponse.<Authorization>builder()
                .content(authPage.getContent())
                .pageNumber(authPage.getNumber())
                .pageSize(authPage.getSize())
                .totalElements(authPage.getTotalElements())
                .totalPages(authPage.getTotalPages())
                .isLast(authPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{authId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật uỷ quyền")
    public ResponseEntity<ApiResponse<Authorization>> updateAuthorization(
            @PathVariable UUID partnerId,
            @PathVariable UUID authId,
            @RequestBody Authorization updateRequest) {
        Authorization updated = partnerService.updateAuthorization(partnerId, authId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{authId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xoá uỷ quyền")
    public ResponseEntity<ApiResponse<Void>> deleteAuthorization(
            @PathVariable UUID partnerId,
            @PathVariable UUID authId) {
        partnerService.deleteAuthorization(partnerId, authId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}