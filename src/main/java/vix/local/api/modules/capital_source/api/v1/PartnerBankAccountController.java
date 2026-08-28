package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.capital_source.application.service.PartnerBankAccountApplicationService;
import vix.local.api.modules.capital_source.domain.model.PartnerBankAccount;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/bank-accounts")
@RequiredArgsConstructor
@Tag(name = "Capital Source", description = "Quản lý nguồn vốn - Đối tác")
public class PartnerBankAccountController {

    private final PartnerBankAccountApplicationService bankAccountService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId(org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.getName() != null) {
            return userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        return null;
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách tài khoản ngân hàng / kênh đặt lệnh của đối tác")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<PartnerBankAccount>>> getBankAccounts(
            @PathVariable UUID partnerId,
            @RequestParam(required = false) String accountType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getBankAccountsByPartnerId(partnerId, accountType, pageable)));
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm tài khoản ngân hàng mới")
    public ResponseEntity<ApiResponse<PartnerBankAccount>> createBankAccount(
            @PathVariable UUID partnerId,
            @RequestBody PartnerBankAccount account,
            org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.createBankAccount(partnerId, account, getCurrentUserId(auth))));
    }

    @PutMapping("/{accountId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật tài khoản ngân hàng")
    public ResponseEntity<ApiResponse<PartnerBankAccount>> updateBankAccount(
            @PathVariable UUID partnerId,
            @PathVariable UUID accountId,
            @RequestBody PartnerBankAccount account,
            org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.updateBankAccount(partnerId, accountId, account, getCurrentUserId(auth))));
    }

    @DeleteMapping("/{accountId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xóa tài khoản ngân hàng (xóa mềm)")
    public ResponseEntity<ApiResponse<Void>> deleteBankAccount(
            @PathVariable UUID partnerId,
            @PathVariable UUID accountId,
            org.springframework.security.core.Authentication auth) {
        bankAccountService.deleteBankAccount(partnerId, accountId, getCurrentUserId(auth));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
