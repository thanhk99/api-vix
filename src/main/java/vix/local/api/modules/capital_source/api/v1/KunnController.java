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

import java.util.List;
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

    @PostMapping("/{id}/approve")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.APPROVE)
    @Operation(summary = "Duyệt Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> approveKunn(
            @PathVariable UUID id) {
        // In a real application, fetch the userId from the authentication context.
        UUID currentUserId = UUID.randomUUID();
        KunnResponseDto response = kunnApplicationService.approveKunn(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/cancel")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.DELETE)
    @Operation(summary = "Huỷ Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<KunnResponseDto>> cancelKunn(
            @PathVariable UUID id) {
        KunnResponseDto response = kunnApplicationService.cancelKunn(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_CONTRACT, action = ActionCode.VIEW)
    @Operation(summary = "Lấy danh sách Khế ước nhận nợ (KUNN)")
    public ResponseEntity<ApiResponse<List<KunnResponseDto>>> getAllKunns() {
        // Note: For a production app, pagination should be used.
        List<KunnResponseDto> response = kunnApplicationService.getAllKunns();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
