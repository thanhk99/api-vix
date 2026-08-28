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
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.api.v1.dto.response.PartnerResponseDto;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.shared.dto.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/capital-source/partners")
@RequiredArgsConstructor
@Tag(name = "Capital Source", description = "Quản lý nguồn vốn")
public class PartnerController {

    private final PartnerApplicationService partnerService;
    private final UserRepository userRepository;

    private PartnerResponseDto mapToDto(Partner partner, Map<UUID, String> userNames) {
        String defaultCreated = partner.getCreatedBy() != null ? "Không tìm thấy user (" + partner.getCreatedBy().toString() + ")" : null;
        String defaultUpdated = partner.getUpdatedBy() != null ? "Không tìm thấy user (" + partner.getUpdatedBy().toString() + ")" : null;
        String defaultApproved = partner.getApprovedBy() != null ? "Không tìm thấy user (" + partner.getApprovedBy().toString() + ")" : null;
        
        String createdByName = partner.getCreatedBy() != null ? userNames.getOrDefault(partner.getCreatedBy(), defaultCreated) : null;
        String updatedByName = partner.getUpdatedBy() != null ? userNames.getOrDefault(partner.getUpdatedBy(), defaultUpdated) : null;
        String approvedByName = partner.getApprovedBy() != null ? userNames.getOrDefault(partner.getApprovedBy(), defaultApproved) : null;

        return PartnerResponseDto.builder()
                .id(partner.getId())
                .cusId(partner.getCusId())
                .branchCusId(partner.getBranchCusId())
                .cusName(partner.getCusName())
                .shortName(partner.getShortName())
                .address(partner.getAddress())
                .idCode(partner.getIdCode())
                .fistIssueDate(partner.getFistIssueDate())
                .lastIssueDate(partner.getLastIssueDate()).changeReason(partner.getChangeReason())
                .issueBy(partner.getIssueBy())
                .changeCount(partner.getChangeCount())
                .opLiscenseNo(partner.getOpLiscenseNo())
                .opIssueDate(partner.getOpIssueDate())
                .mobile(partner.getMobile())
                .email(partner.getEmail())
                .website(partner.getWebsite())
                .cusType(partner.getCusType())
                .businessType(partner.getBusinessType())
                .professionalInvestor(partner.getProfessionalInvestor())
                .professionalStartDate(partner.getProfessionalStartDate())
                .professionalEndDate(partner.getProfessionalEndDate())
                .depositoryMemberCode(partner.getDepositoryMemberCode())
                .tradingGateway(partner.getTradingGateway())
                .status(partner.getStatus())
                .isActive(partner.getIsActive())
                .createdBy(createdByName)
                .updatedBy(updatedByName)
                .lastUpdated(partner.getLastUpdated())
                .approvedBy(approvedByName)
                .approvedAt(partner.getApprovedAt())
                .totalPool(partner.getTotalPool() != null ? partner.getTotalPool() : java.math.BigDecimal.ZERO)
                .usedPool(partner.getUsedPool() != null ? partner.getUsedPool() : java.math.BigDecimal.ZERO)
                .remainPool(partner.getRemainPool() != null ? partner.getRemainPool() : java.math.BigDecimal.ZERO)
                .build();
    }


    private Map<UUID, String> getAllUserNames() {
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(
                    User::getId, 
                    u -> u.getFullName() != null ? u.getFullName() : u.getEmail() != null ? u.getEmail() : u.getId().toString(), 
                    (existing, replacement) -> existing
                ));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách đối tác")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<PartnerResponseDto>>> getAllPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<Partner> partnerPage = partnerService.getAllPartners(pageable);
        
        Map<UUID, String> userNames = getAllUserNames();
        List<PartnerResponseDto> responseContent = partnerPage.getContent().stream()
                .map(partner -> mapToDto(partner, userNames))
                .toList();
                
        vix.local.api.shared.dto.PagedResponse<PartnerResponseDto> pagedResponse = vix.local.api.shared.dto.PagedResponse.<PartnerResponseDto>builder()
                .content(responseContent)
                .pageNumber(partnerPage.getNumber())
                .pageSize(partnerPage.getSize())
                .totalElements(partnerPage.getTotalElements())
                .totalPages(partnerPage.getTotalPages())
                .isLast(partnerPage.isLast())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    @Operation(summary = "Thêm mới đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> createPartner(@RequestBody Partner partner) {
        Partner created = partnerService.createPartner(partner);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(created, userNames)));
    }

    @GetMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    @Operation(summary = "Lấy chi tiết đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> getPartner(@PathVariable UUID id) {
        Partner partner = partnerService.getPartner(id);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(partner, userNames)));
    }

    @PutMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật thông tin chung đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> updatePartner(
            @PathVariable UUID id,
            @RequestBody Partner updateRequest) {
        Partner updated = partnerService.updatePartner(id, updateRequest);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated, userNames)));
    }

    @PatchMapping("/{id}/customer-type")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật loại hình khách hàng của đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> updateCustomerType(
            @PathVariable UUID id,
            @RequestBody Partner updateRequest) {
        Partner updated = partnerService.updateCustomerType(id, updateRequest);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated, userNames)));
    }

    @DeleteMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.DELETE)
    @Operation(summary = "Xoá đối tác (Xoá mềm)")
    public ResponseEntity<ApiResponse<Void>> deletePartner(@PathVariable UUID id) {
        partnerService.deletePartner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.APPROVE)
    @Operation(summary = "Phê duyệt đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> approvePartner(
            @PathVariable UUID id,
            org.springframework.security.core.Authentication auth) {
        UUID approverId = null;
        if (auth != null && auth.getName() != null) {
            approverId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        Partner approved = partnerService.approvePartner(id, approverId);
        
        Map<UUID, String> userNames = getAllUserNames();
        
        return ResponseEntity.ok(ApiResponse.success(mapToDto(approved, userNames)));
    }

    @PutMapping("/{id}/reject")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> rejectPartner(
            @PathVariable UUID id,
            @RequestBody(required = false) java.util.Map<String, Object> body,
            org.springframework.security.core.Authentication auth) {
        UUID rejecterId = null;
        if (auth != null && auth.getName() != null) {
            rejecterId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        Partner rejected = partnerService.rejectPartner(id, rejecterId, body);
        
        Map<UUID, String> userNames = getAllUserNames();
        
        return ResponseEntity.ok(ApiResponse.success(mapToDto(rejected, userNames)));
    }
    
    @PutMapping("/{id}/approve-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt xoá đối tác")
    public ResponseEntity<ApiResponse<Void>> approveDeletePartner(@PathVariable UUID id) {
        partnerService.approveDeletePartner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/reject-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối xóa Đối tác")
    public ResponseEntity<ApiResponse<Void>> rejectDeletePartner(@PathVariable UUID id) {
        partnerService.rejectDeletePartner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{id}/pool")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.UPDATE)
    @Operation(summary = "Thiết lập tổng hạn mức Đối tác")
    public ResponseEntity<ApiResponse<PartnerResponseDto>> setPartnerPool(
            @PathVariable UUID id,
            @RequestParam java.math.BigDecimal totalPool) {
        Partner partner = partnerService.setPartnerPool(id, totalPool);
        Map<UUID, String> userNames = getAllUserNames();
        return ResponseEntity.ok(ApiResponse.success(mapToDto(partner, userNames)));
    }
}
