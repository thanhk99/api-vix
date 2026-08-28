package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;

import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitResponseDto;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/capital-source/contracts/{contractId}/credit-limits")
@RequiredArgsConstructor
@Tag(name = "Capital Source")
public class CreditLimitController {

    private final PartnerApplicationService partnerService;
    private final UserRepository userRepository;
    private final vix.local.api.modules.capital_source.domain.repository.CreditContractRepository contractRepository;
    private final vix.local.api.modules.capital_source.domain.repository.PartnerRepository partnerRepository;

    private Map<UUID, String> getAllUserNames() {
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(
                    User::getId, 
                    u -> u.getFullName() != null ? u.getFullName() : u.getEmail() != null ? u.getEmail() : u.getId().toString(), 
                    (existing, replacement) -> existing
                ));
    }

    private CreditLimitResponseDto mapToDto(CreditLimit limit, Map<UUID, String> userNames) {
        String defaultApproved = limit.getApprovedBy() != null ? "Không tìm thấy user (" + limit.getApprovedBy().toString() + ")" : null;
        String approvedByName = limit.getApprovedBy() != null ? userNames.getOrDefault(limit.getApprovedBy(), defaultApproved) : null;

        vix.local.api.modules.capital_source.domain.model.Partner partner = limit.getPartnerId() != null 
                ? partnerRepository.findById(limit.getPartnerId()) : null;

        String branchCusId = partner != null ? partner.getBranchCusId() : null;
        String cusName = partner != null ? partner.getCusName() : null;

        return CreditLimitResponseDto.builder()
                .id(limit.getId())
                .partnerId(limit.getPartnerId())
                .branchCusId(branchCusId)
                .cusName(cusName)
                .contractId(limit.getContractId())
                .contractNo(limit.getContractId() != null ? java.util.Optional.ofNullable(contractRepository.findById(limit.getContractId())).map(vix.local.api.modules.capital_source.domain.model.CreditContract::getContractNo).orElse(null) : null)
                .limitId(limit.getLimitId())
                .poolName(limit.getPoolName())
                .currency(limit.getCurrency())
                .poolType(limit.getPoolType())
                .totalPool(limit.getTotalPool())
                .usedPool(limit.getUsedPool())
                .remainPool(limit.getRemainPool())
                .startDate(limit.getStartDate())
                .endDate(limit.getEndDate())
                .status(limit.getEffectiveStatus())
                .createdAt(limit.getCreatedAt())
                .updatedAt(limit.getUpdatedAt())
                .approvedBy(approvedByName)
                .approvedAt(limit.getApprovedAt())
                .creditRatio(limit.getCreditRatio())
                .purpose(limit.getPurpose())
                .hasCollateral(false)
                .children(null)
                .build();
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.CREATE)
    @Operation(summary = "Thêm hạn mức tín dụng cho đối tác")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> createCreditLimit(
            @PathVariable UUID contractId,
            @RequestBody CreditLimit creditLimit) {
        CreditLimit created = partnerService.createCreditLimit(contractId, creditLimit);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(created, userNames)));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách hạn mức tín dụng của đối tác")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<CreditLimitResponseDto>>> getCreditLimits(
            @PathVariable UUID contractId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<CreditLimit> limitPage = partnerService.getCreditLimitsByPartnerId(contractId, pageable);

        
        Map<UUID, String> userNames = getAllUserNames();
        List<CreditLimitResponseDto> responseContent = limitPage.getContent().stream()
                .map(limit -> mapToDto(limit, userNames))
                .toList();
                
        vix.local.api.shared.dto.PagedResponse<CreditLimitResponseDto> pagedResponse = vix.local.api.shared.dto.PagedResponse.<CreditLimitResponseDto>builder()
                .content(responseContent)
                .pageNumber(limitPage.getNumber())
                .pageSize(limitPage.getSize())
                .totalElements(limitPage.getTotalElements())
                .totalPages(limitPage.getTotalPages())
                .isLast(limitPage.isLast())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{limitId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật hạn mức tín dụng")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> updateCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            @RequestBody CreditLimit updateRequest) {
        CreditLimit updated = partnerService.updateCreditLimit(contractId, limitId, updateRequest);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated, userNames)));
    }

    @DeleteMapping("/{limitId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.DELETE)
    @Operation(summary = "Xoá hạn mức tín dụng (Xoá mềm)")
    public ResponseEntity<ApiResponse<Void>> deleteCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId) {
        partnerService.deleteCreditLimit(contractId, limitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{limitId}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Phê duyệt toàn bộ hạn mức tín dụng của đối tác")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> approveCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            org.springframework.security.core.Authentication auth) {
        
        UUID approverId = null;
        if (auth != null && auth.getName() != null) {
            approverId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        
        CreditLimit approved = partnerService.approveCreditLimit(contractId, limitId, approverId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(approved, userNames)));
    }

    @PutMapping("/{limitId}/reject")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối toàn bộ hạn mức tín dụng của đối tác")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> rejectCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            org.springframework.security.core.Authentication auth) {
        
        UUID rejecterId = null;
        if (auth != null && auth.getName() != null) {
            rejecterId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        
        CreditLimit rejected = partnerService.rejectCreditLimit(contractId, limitId, rejecterId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(rejected, userNames)));
    }

    @PostMapping("/{limitId}/transactions/increase")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.UPDATE)
    @Operation(summary = "Tăng hạn mức tín dụng")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> increaseCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String transactionType,
            @RequestParam(required = false) String referenceId,
            org.springframework.security.core.Authentication auth) {
        
        UUID creatorId = auth != null && auth.getName() != null ? userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null) : null;
        CreditLimit updated = partnerService.increaseCreditLimit(contractId, limitId, amount, transactionType, referenceId, creatorId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated, userNames)));
    }

    @PostMapping("/{limitId}/transactions/decrease")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.UPDATE)
    @Operation(summary = "Giảm hạn mức tín dụng")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> decreaseCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String transactionType,
            @RequestParam(required = false) String referenceId,
            org.springframework.security.core.Authentication auth) {
        
        UUID creatorId = auth != null && auth.getName() != null ? userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null) : null;
        CreditLimit updated = partnerService.decreaseCreditLimit(contractId, limitId, amount, transactionType, referenceId, creatorId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated, userNames)));
    }

    @GetMapping("/{limitId}/transactions/history")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "Tra cứu lịch sử tăng giảm hạn mức tín dụng")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory>>> getCreditLimitHistory(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fromDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory> historyPage = partnerService.searchCreditLimitHistory(limitId, fromDate, toDate, pageable);
        
        vix.local.api.shared.dto.PagedResponse<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory> pagedResponse = vix.local.api.shared.dto.PagedResponse.<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory>builder()
                .content(historyPage.getContent())
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .isLast(historyPage.isLast())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/{limitId}/approve-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt xoá hạn mức tín dụng")
    public ResponseEntity<ApiResponse<Void>> approveDeleteCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId) {
        partnerService.approveDeleteCreditLimit(contractId, limitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{limitId}/reject-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối xoá hạn mức tín dụng")
    public ResponseEntity<ApiResponse<Void>> rejectDeleteCreditLimit(
            @PathVariable UUID contractId,
            @PathVariable UUID limitId) {
        partnerService.rejectDeleteCreditLimit(contractId, limitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
