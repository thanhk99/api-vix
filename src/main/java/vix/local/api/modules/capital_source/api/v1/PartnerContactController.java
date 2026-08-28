package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.capital_source.application.service.PartnerContactApplicationService;
import vix.local.api.modules.capital_source.domain.model.PartnerContact;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/contacts")
@RequiredArgsConstructor
@Tag(name = "Capital Source", description = "Quản lý nguồn vốn - Đối tác")
public class PartnerContactController {

    private final PartnerContactApplicationService contactService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId(org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.getName() != null) {
            return userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        return null;
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách người liên hệ của đối tác")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<PartnerContact>>> getContacts(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(contactService.getContactsByPartnerId(partnerId, pageable)));
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm người liên hệ mới")
    public ResponseEntity<ApiResponse<PartnerContact>> createContact(
            @PathVariable UUID partnerId,
            @RequestBody PartnerContact contact,
            org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(contactService.createContact(partnerId, contact, getCurrentUserId(auth))));
    }

    @PutMapping("/{contactId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật người liên hệ")
    public ResponseEntity<ApiResponse<PartnerContact>> updateContact(
            @PathVariable UUID partnerId,
            @PathVariable UUID contactId,
            @RequestBody PartnerContact contact,
            org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(contactService.updateContact(partnerId, contactId, contact, getCurrentUserId(auth))));
    }

    @DeleteMapping("/{contactId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xóa người liên hệ (xóa mềm)")
    public ResponseEntity<ApiResponse<Void>> deleteContact(
            @PathVariable UUID partnerId,
            @PathVariable UUID contactId,
            org.springframework.security.core.Authentication auth) {
        contactService.deleteContact(partnerId, contactId, getCurrentUserId(auth));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
