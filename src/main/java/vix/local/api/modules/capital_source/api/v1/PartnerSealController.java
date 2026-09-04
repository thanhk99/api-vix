package vix.local.api.modules.capital_source.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.capital_source.application.service.PartnerSealApplicationService;
import vix.local.api.modules.capital_source.domain.model.PartnerSeal;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/seals")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class PartnerSealController {

    private final PartnerSealApplicationService partnerSealService;
    private final UserRepository userRepository;

    private UUID getUserIdFromAuth(Authentication auth) {
        if (auth != null && auth.getName() != null) {
            return userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        return null;
    }

    private void populateUserNames(java.util.List<PartnerSeal> seals) {
        if (seals == null || seals.isEmpty()) return;
        java.util.Map<UUID, String> cache = new java.util.HashMap<>();
        seals.forEach(s -> {
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
    @Operation(summary = "Thêm mẫu dấu cho đối tác")
    public ResponseEntity<ApiResponse<PartnerSeal>> createSeal(
            @PathVariable UUID partnerId,
            @RequestBody PartnerSeal seal,
            Authentication auth) {
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSeal created = partnerSealService.createSeal(partnerId, seal, updaterId);
        populateUserNames(java.util.Collections.singletonList(created));
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách mẫu dấu của đối tác")
    public ResponseEntity<ApiResponse<PagedResponse<PartnerSeal>>> getSeals(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<PartnerSeal> sealPage = partnerSealService.getSealsByPartnerId(partnerId, pageable);
        populateUserNames(sealPage.getContent());

        PagedResponse<PartnerSeal> pagedResponse = PagedResponse.<PartnerSeal>builder()
                .content(sealPage.getContent())
                .pageNumber(sealPage.getNumber())
                .pageSize(sealPage.getSize())
                .totalElements(sealPage.getTotalElements())
                .totalPages(sealPage.getTotalPages())
                .isLast(sealPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{sealId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật mẫu dấu")
    public ResponseEntity<ApiResponse<PartnerSeal>> updateSeal(
            @PathVariable UUID partnerId,
            @PathVariable UUID sealId,
            @RequestBody PartnerSeal updateRequest,
            Authentication auth) {
        UUID updaterId = getUserIdFromAuth(auth);
        PartnerSeal updated = partnerSealService.updateSeal(partnerId, sealId, updateRequest, updaterId);
        populateUserNames(java.util.Collections.singletonList(updated));
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{sealId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xoá mẫu dấu")
    public ResponseEntity<ApiResponse<Void>> deleteSeal(
            @PathVariable UUID partnerId,
            @PathVariable UUID sealId) {
        partnerSealService.deleteSeal(partnerId, sealId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}