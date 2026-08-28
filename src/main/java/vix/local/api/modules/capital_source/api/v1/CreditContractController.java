package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.capital_source.application.service.CreditContractApplicationService;
import vix.local.api.modules.capital_source.domain.model.CreditContract;
import vix.local.api.modules.capital_source.api.v1.dto.request.CreditContractRequestDto;
import vix.local.api.modules.capital_source.api.v1.dto.response.CreditContractResponseDto;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.identity.domain.model.User;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/capital-source")
@RequiredArgsConstructor
@Tag(name = "Capital Source - Credit Contract")
public class CreditContractController {

    private final CreditContractApplicationService contractService;
    private final UserRepository userRepository;

    private CreditContractResponseDto mapToDto(CreditContract contract) {
        return CreditContractResponseDto.builder()
                .id(contract.getId())
                .partnerId(contract.getPartnerId())
                .contractNo(contract.getContractNo())
                .totalLimit(contract.getTotalLimit())
                .usedLimit(contract.getUsedLimit())
                .remainLimit(contract.getRemainLimit())
                .purpose(contract.getPurpose())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .approvedBy(contract.getApprovedBy())
                .approvedAt(contract.getApprovedAt())
                .build();
    }

    @PostMapping("/partners/{partnerId}/contracts")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.CREATE)
    @Operation(summary = "Thêm hợp đồng tín dụng cho đối tác")
    public ResponseEntity<ApiResponse<CreditContractResponseDto>> createContract(
            @PathVariable UUID partnerId,
            @RequestBody CreditContractRequestDto request) {
        CreditContract contract = CreditContract.builder()
                .contractNo(request.getContractNo())
                .totalLimit(request.getTotalLimit())
                .purpose(request.getPurpose())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        CreditContract created = contractService.createContract(partnerId, contract);
        return ResponseEntity.ok(ApiResponse.success(mapToDto(created)));
    }

    @GetMapping("/partners/{partnerId}/contracts")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách hợp đồng của đối tác")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<CreditContractResponseDto>>> getContracts(
            @PathVariable UUID partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<CreditContract> contractPage = contractService.getContractsByPartnerId(partnerId, pageable);
        
        java.util.List<CreditContractResponseDto> responseContent = contractPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
                
        vix.local.api.shared.dto.PagedResponse<CreditContractResponseDto> pagedResponse = vix.local.api.shared.dto.PagedResponse.<CreditContractResponseDto>builder()
                .content(responseContent)
                .pageNumber(contractPage.getNumber())
                .pageSize(contractPage.getSize())
                .totalElements(contractPage.getTotalElements())
                .totalPages(contractPage.getTotalPages())
                .isLast(contractPage.isLast())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PutMapping("/contracts/{contractId}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật hợp đồng")
    public ResponseEntity<ApiResponse<CreditContractResponseDto>> updateContract(
            @PathVariable UUID contractId,
            @RequestBody CreditContractRequestDto request) {
        CreditContract updateModel = CreditContract.builder()
                .contractNo(request.getContractNo())
                .totalLimit(request.getTotalLimit())
                .purpose(request.getPurpose())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        CreditContract updated = contractService.updateContract(contractId, updateModel);
        return ResponseEntity.ok(ApiResponse.success(mapToDto(updated)));
    }

    @PutMapping("/contracts/{contractId}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_LIMIT, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt hợp đồng")
    public ResponseEntity<ApiResponse<CreditContractResponseDto>> approveContract(
            @PathVariable UUID contractId,
            org.springframework.security.core.Authentication auth) {
        
        UUID approverId = null;
        if (auth != null && auth.getName() != null) {
            approverId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        
        CreditContract approved = contractService.approveContract(contractId, approverId);
        return ResponseEntity.ok(ApiResponse.success(mapToDto(approved)));
    }
}
