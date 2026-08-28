package vix.local.api.modules.capital_source.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.capital_source.application.service.PartnerSecuritiesAccountApplicationService;
import vix.local.api.modules.capital_source.domain.model.PartnerSecuritiesAccount;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/securities-accounts")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class PartnerSecuritiesAccountController {

    private final PartnerSecuritiesAccountApplicationService accountService;

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm tài khoản chứng khoán cho đối tác")
    public ResponseEntity<ApiResponse<PartnerSecuritiesAccount>> createAccount(
            @PathVariable UUID partnerId,
            @RequestBody PartnerSecuritiesAccount account) {
        PartnerSecuritiesAccount created = accountService.createAccount(partnerId, account);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách tài khoản chứng khoán của đối tác")
    public ResponseEntity<ApiResponse<PagedResponse<PartnerSecuritiesAccount>>> getAccounts(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<PartnerSecuritiesAccount> accountPage = accountService.getAccountsByPartnerId(partnerId, pageable);

        PagedResponse<PartnerSecuritiesAccount> pagedResponse = PagedResponse.<PartnerSecuritiesAccount>builder()
                .content(accountPage.getContent())
                .pageNumber(accountPage.getNumber())
                .pageSize(accountPage.getSize())
                .totalElements(accountPage.getTotalElements())
                .totalPages(accountPage.getTotalPages())
                .isLast(accountPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{accountId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật tài khoản chứng khoán")
    public ResponseEntity<ApiResponse<PartnerSecuritiesAccount>> updateAccount(
            @PathVariable UUID partnerId,
            @PathVariable UUID accountId,
            @RequestBody PartnerSecuritiesAccount updateRequest) {
        PartnerSecuritiesAccount updated = accountService.updateAccount(partnerId, accountId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{accountId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xoá tài khoản chứng khoán")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable UUID partnerId,
            @PathVariable UUID accountId) {
        accountService.deleteAccount(partnerId, accountId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}