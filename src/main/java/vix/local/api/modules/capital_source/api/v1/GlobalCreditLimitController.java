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
@RequestMapping("/v1/capital-source/credit-limits")
@RequiredArgsConstructor
@Tag(name = "Capital Source - Global Credit Limits")
public class GlobalCreditLimitController {

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

    private CreditLimitResponseDto mapToDto(CreditLimit limit, Map<UUID, String> userNames, List<CreditLimitResponseDto> children) {
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
                .children(children)
                .build();
    }


    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.CREATE)
    @Operation(summary = "Tạo Hạn mức (và Hợp đồng nếu chưa có) cùng lúc")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> createGlobalCreditLimit(
            @RequestBody vix.local.api.modules.capital_source.api.v1.dto.request.GlobalCreditLimitRequestDto requestDto) {
        
        CreditLimit created = partnerService.createGlobalCreditLimit(requestDto);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(created, userNames, null)));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "LÃ¡ÂºÂ¥y danh sÃƒÂ¡ch tÃ¡Â»â€¢ng hÃ¡Â»Â£p tÃ¡ÂºÂ¥t cÃ¡ÂºÂ£ hÃ¡ÂºÂ¡n mÃ¡Â»Â©c tÃƒÂ­n dÃ¡Â»Â¥ng (DÃ¡ÂºÂ¡ng CÃƒÂ¢y)")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<CreditLimitResponseDto>>> getGlobalCreditLimits(
            @RequestParam(required = false) UUID partnerId,
            @RequestParam(required = false) String limitId,
            @RequestParam(required = false) String limitType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<CreditLimit> limitPage = partnerService.searchGlobalCreditLimits(
                partnerId, limitId,limitType, status, startDate, endDate, pageable);

        Map<UUID, String> userNames = getAllUserNames();
        
        // Fetch all children for the current page's parent limits
        List<UUID> contractIds = limitPage.getContent().stream().map(CreditLimit::getId).toList();
        List<CreditLimit> allChildren = partnerService.findChildrenBycontractIds(contractIds);
        
        // Group children by contractId
        Map<UUID, List<CreditLimit>> childrenBycontractId = allChildren.stream()
                .filter(c -> c.getContractId() != null)
                .collect(Collectors.groupingBy(c -> c.getContractId() != null ? c.getContractId() : UUID.randomUUID()));

        // Build the tree
        List<CreditLimitResponseDto> responseContent = limitPage.getContent().stream()
                .map(parent -> {
                    List<CreditLimit> children = childrenBycontractId.getOrDefault(parent.getId(), List.of());
                    List<CreditLimitResponseDto> childrenDtos = children.stream()
                            .map(child -> mapToDto(child, userNames, null))
                            .toList();
                    return mapToDto(parent, userNames, childrenDtos.isEmpty() ? null : childrenDtos);
                })
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

    @PutMapping("/{limitId}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "PhÃƒÂª duyÃ¡Â»â€¡t toÃƒÂ n bÃ¡Â»â„¢ hÃ¡ÂºÂ¡n mÃ¡Â»Â©c cÃ¡Â»Â§a Ã„â€˜Ã¡Â»â€˜i tÃƒÂ¡c thÃƒÂ´ng qua 1 hÃ¡ÂºÂ¡n mÃ¡Â»Â©c")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> approveCreditLimit(
            @PathVariable UUID limitId,
            org.springframework.security.core.Authentication auth) {
        
        UUID approverId = null;
        if (auth != null && auth.getName() != null) {
            approverId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        
        CreditLimit approved = partnerService.approveCreditLimit(null, limitId, approverId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(approved, userNames, null)));
    }

    @PutMapping("/{limitId}/reject")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "TÃ¡Â»Â« chÃ¡Â»â€˜i toÃƒÂ n bÃ¡Â»â„¢ hÃ¡ÂºÂ¡n mÃ¡Â»Â©c cÃ¡Â»Â§a Ã„â€˜Ã¡Â»â€˜i tÃƒÂ¡c thÃƒÂ´ng qua 1 hÃ¡ÂºÂ¡n mÃ¡Â»Â©c")
    public ResponseEntity<ApiResponse<CreditLimitResponseDto>> rejectCreditLimit(
            @PathVariable UUID limitId,
            org.springframework.security.core.Authentication auth) {
        
        UUID rejecterId = null;
        if (auth != null && auth.getName() != null) {
            rejecterId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        
        CreditLimit rejected = partnerService.rejectCreditLimit(null, limitId, rejecterId);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(rejected, userNames, null)));
    }

    @PutMapping("/{limitId}/approve-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "DuyÃ¡Â»â€¡t xoÃƒÂ¡ hÃ¡ÂºÂ¡n mÃ¡Â»Â©c tÃƒÂ­n dÃ¡Â»Â¥ng")
    public ResponseEntity<ApiResponse<Void>> approveDeleteCreditLimit(
            @PathVariable UUID limitId) {
        partnerService.approveDeleteCreditLimit(null, limitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{limitId}/reject-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối xoá hạn mức tín dụng")
    public ResponseEntity<ApiResponse<Void>> rejectDeleteCreditLimit(
            @PathVariable UUID limitId) {
        partnerService.rejectDeleteCreditLimit(null, limitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private String formatVietnameseReason(String type, String refId) {
        if (refId != null && !refId.isBlank() && (refId.contains(" ") || refId.contains("Thiết lập") || refId.contains("Điều chỉnh") || refId.contains("Trả nợ") || refId.contains("KUNN"))) {
            return refId;
        }
        if (type == null) return refId != null ? refId : "Biến động hạn mức";
        return switch (type.toUpperCase()) {
            case "INITIAL_SETUP" -> "Thiết lập hạn mức ban đầu";
            case "MANUAL_INC" -> "Tăng hạn mức manual (User chủ động nới HM)" + (refId != null ? " - " + refId : "");
            case "MANUAL_DEC" -> "Giảm hạn mức manual (User chủ động thu HM)" + (refId != null ? " - " + refId : "");
            case "DEBT_REPAY" -> "Giao dịch trả nợ (Thu nợ gốc)" + (refId != null ? " - " + refId : "");
            case "NEW_LOAN" -> "Phát sinh KUNN (Giải ngân)" + (refId != null ? " - " + refId : "");
            case "ASSET_MORTGAGE" -> "Giao dịch Cầm cố thế chấp thêm TSĐB" + (refId != null ? " - " + refId : "");
            case "ASSET_RELEASE" -> "Giao dịch Giải tỏa TSĐB" + (refId != null ? " - " + refId : "");
            case "ASSET_REVAL_INC" -> "Giao dịch Đánh giá lại TSĐB (Tăng giá)" + (refId != null ? " - " + refId : "");
            case "ASSET_REVAL_DEC" -> "Giao dịch Đánh giá lại TSĐB (Giảm giá)" + (refId != null ? " - " + refId : "");
            default -> (refId != null && !refId.isBlank()) ? refId : type;
        };
    }

    @GetMapping("/global-history")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "Tra cứu lịch sử tăng giảm hạn mức tín dụng toàn hệ thống")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitHistoryResponseDto>>> getGlobalCreditLimitHistory(
            @RequestParam(required = false) UUID partnerId,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String limitType,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fromDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        java.time.LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        java.time.LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59, 999999999) : null;

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<vix.local.api.modules.capital_source.domain.model.CreditLimitHistory> historyPage = partnerService.searchGlobalCreditLimitHistory(
                partnerId, contractNo, limitType, reason, fromDateTime, toDateTime, pageable);

        Map<UUID, String> userNames = getAllUserNames();
        Map<UUID, CreditLimit> limitMap = partnerService.getAllCreditLimits().stream()
                .collect(Collectors.toMap(CreditLimit::getId, l -> l, (a, b) -> a));

        List<vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitHistoryResponseDto> content = historyPage.getContent().stream()
                .map(h -> {
                    CreditLimit lim = limitMap.get(h.getCreditLimitId());
                    String cNo = lim != null && lim.getContractId() != null 
                            ? java.util.Optional.ofNullable(contractRepository.findById(lim.getContractId())).map(vix.local.api.modules.capital_source.domain.model.CreditContract::getContractNo).orElse("-")
                            : "-";
                    String lType = lim != null ? lim.getPoolType() : "-";
                    
                    var partner = lim != null && lim.getPartnerId() != null ? partnerRepository.findById(lim.getPartnerId()) : null;
                    String branchCusId = partner != null ? partner.getBranchCusId() : null;
                    String cusName = partner != null ? partner.getCusName() : null;

                    java.math.BigDecimal inc = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal dec = java.math.BigDecimal.ZERO;
                    String type = h.getTransactionType() != null ? h.getTransactionType() : "";
                    if ("MANUAL_INC".equalsIgnoreCase(type) || "DEBT_REPAY".equalsIgnoreCase(type) || "ASSET_REVAL_INC".equalsIgnoreCase(type) || "ASSET_MORTGAGE".equalsIgnoreCase(type) || "INITIAL_SETUP".equalsIgnoreCase(type)) {
                        inc = h.getAmount() != null ? h.getAmount() : java.math.BigDecimal.ZERO;
                    } else if ("MANUAL_DEC".equalsIgnoreCase(type) || "NEW_LOAN".equalsIgnoreCase(type) || "ASSET_REVAL_DEC".equalsIgnoreCase(type) || "ASSET_RELEASE".equalsIgnoreCase(type)) {
                        dec = h.getAmount() != null ? h.getAmount() : java.math.BigDecimal.ZERO;
                    }

                    String vietnameseReason = formatVietnameseReason(type, h.getReferenceId());

                    return vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitHistoryResponseDto.builder()
                            .id(h.getId())
                            .creditLimitId(h.getCreditLimitId())
                            .contactNo(cNo)
                            .limitType(lType)
                            .branchCusId(branchCusId)
                            .cusName(cusName)
                            .transactionType(h.getTransactionType())
                            .amount(h.getAmount())
                            .initialLimit(h.getPreTotalPool() != null ? h.getPreTotalPool() : h.getNewTotalPool())
                            .increaseAmount(inc)
                            .decreaseAmount(dec)
                            .remainLimit(h.getNewRemainPool())
                            .reason(vietnameseReason)
                            .referenceId(h.getReferenceId())
                            .transactionDate(h.getTransactionDate() != null ? h.getTransactionDate() : h.getCreatedAt())
                            .createdAt(h.getCreatedAt())
                            .createdBy(h.getCreatedBy() != null ? userNames.getOrDefault(h.getCreatedBy(), h.getCreatedBy().toString()) : null)
                            .build();
                })
                .toList();

        vix.local.api.shared.dto.PagedResponse<vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitHistoryResponseDto> response = vix.local.api.shared.dto.PagedResponse.<vix.local.api.modules.capital_source.api.v1.dto.response.CreditLimitHistoryResponseDto>builder()
                .content(content)
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .isLast(historyPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
