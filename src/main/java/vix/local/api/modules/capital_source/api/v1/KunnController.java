package vix.local.api.modules.capital_source.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.capital_source.api.v1.dto.request.KunnRequestDto;
import vix.local.api.modules.capital_source.api.v1.dto.response.KunnResponseDto;
import vix.local.api.modules.capital_source.application.service.KunnApplicationService;
import vix.local.api.shared.dto.ApiResponse;

import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;

import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/kunns")
@RequiredArgsConstructor
public class KunnController {

    private final KunnApplicationService kunnApplicationService;

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.CREATE)
    @Operation(summary = "Tạo mới Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> createKunn(
            @RequestBody KunnRequestDto requestDto) {
        // In a real application, fetch the userId from the authentication context.
        // Using a dummy UUID for the example.
        UUID currentUserId = UUID.randomUUID();
        KunnResponseDto response = kunnApplicationService.createKunn(requestDto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.UPDATE)
    @Operation(summary = "Cập nhật Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> updateKunn(
            @PathVariable UUID id,
            @RequestBody KunnRequestDto requestDto) {
        KunnResponseDto response = kunnApplicationService.updateKunn(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> approveKunn(
            @PathVariable UUID id) {
        // In a real application, fetch the userId from the authentication context.
        UUID currentUserId = UUID.randomUUID();
        KunnResponseDto response = kunnApplicationService.approveKunn(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.DELETE)
    @Operation(summary = "Huỷ Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> cancelKunn(
            @PathVariable UUID id) {
        KunnResponseDto response = kunnApplicationService.cancelKunn(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.VIEW)
    @Operation(summary = "Chi tiết Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> getKunnById(@PathVariable UUID id) {
        KunnResponseDto response = kunnApplicationService.getKunnById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<vix.local.api.shared.dto.PagedResponse<KunnResponseDto>>> getAllKunns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "createdDate"));
        org.springframework.data.domain.Page<KunnResponseDto> pageData = kunnApplicationService.getAllKunns(pageable);

        vix.local.api.shared.dto.PagedResponse<KunnResponseDto> response = vix.local.api.shared.dto.PagedResponse
                .<KunnResponseDto>builder()
                .content(pageData.getContent())
                .pageNumber(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/approve-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt huỷ Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> approveCancelKunn(
            @PathVariable UUID id) {
        KunnResponseDto response = kunnApplicationService.approveCancelKunn(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/reject-delete")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.APPROVE)
    @Operation(summary = "Từ chối huỷ Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> rejectCancelKunn(
            @PathVariable UUID id) {
        KunnResponseDto response = kunnApplicationService.rejectCancelKunn(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
