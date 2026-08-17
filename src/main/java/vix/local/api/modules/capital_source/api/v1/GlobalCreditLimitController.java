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

        return CreditLimitResponseDto.builder()
                .id(limit.getId())
                .partnerId(limit.getPartnerId())
                .parentId(limit.getParentId())
                .limitId(limit.getLimitId())
                .poolName(limit.getPoolName())
                .currency(limit.getCurrency())
                .poolType(limit.getPoolType())
                .totalPool(limit.getTotalPool())
                .usedPool(limit.getUsedPool())
                .remainPool(limit.getRemainPool())
                .startDate(limit.getStartDate())
                .endDate(limit.getEndDate())
                .status(limit.getStatus())
                .createdAt(limit.getCreatedAt())
                .updatedAt(limit.getUpdatedAt())
                .approvedBy(approvedByName)
                .approvedAt(limit.getApprovedAt())
                .contactNo(limit.getContactNo())
                .creditRatio(limit.getCreditRatio())
                .purpose(limit.getPurpose())
                .hasCollateral(false)
                .children(children)
                .build();
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách tổng hợp tất cả hạn mức tín dụng (Dạng Cây)")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<CreditLimitResponseDto>>> getGlobalCreditLimits(
            @RequestParam(required = false) UUID partnerId,
            @RequestParam(required = false) String limitId,
            @RequestParam(required = false) String contactNo,
            @RequestParam(required = false) String limitType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<CreditLimit> limitPage = partnerService.searchGlobalCreditLimits(
                partnerId, limitId, contactNo, limitType, status, startDate, endDate, pageable);

        Map<UUID, String> userNames = getAllUserNames();
        
        // Fetch all children for the current page's parent limits
        List<UUID> parentIds = limitPage.getContent().stream().map(CreditLimit::getId).toList();
        List<CreditLimit> allChildren = partnerService.findChildrenByParentIds(parentIds);
        
        // Group children by parentId
        Map<UUID, List<CreditLimit>> childrenByParentId = allChildren.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(CreditLimit::getParentId));

        // Build the tree
        List<CreditLimitResponseDto> responseContent = limitPage.getContent().stream()
                .map(parent -> {
                    List<CreditLimit> children = childrenByParentId.getOrDefault(parent.getId(), List.of());
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
}
