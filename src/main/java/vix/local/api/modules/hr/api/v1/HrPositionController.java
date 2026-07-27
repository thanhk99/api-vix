package vix.local.api.modules.hr.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.hr.api.v1.dto.request.CreatePositionRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdatePositionRequest;
import vix.local.api.modules.hr.api.v1.dto.response.PositionResponse;
import vix.local.api.modules.hr.application.service.HrPositionApplicationService;
import vix.local.api.modules.hr.domain.model.HrPosition;
import vix.local.api.shared.dto.ApiResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/hr/positions")
@RequiredArgsConstructor
@Tag(name = "HR Position Management", description = "Quản lý chức danh")
public class HrPositionController {

    private final HrPositionApplicationService hrPositionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Lấy danh sách chức danh")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getAllPositions() {
        List<PositionResponse> list = hrPositionService.getAllPositions().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Lấy chi tiết chức danh")
    public ResponseEntity<ApiResponse<PositionResponse>> getPositionById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(hrPositionService.getPositionById(id))));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Tạo chức danh mới")
    public ResponseEntity<ApiResponse<PositionResponse>> createPosition(@Valid @RequestBody CreatePositionRequest request) {
        HrPosition pos = hrPositionService.createPosition(request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(pos)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Cập nhật chức danh")
    public ResponseEntity<ApiResponse<PositionResponse>> updatePosition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePositionRequest request) {
        HrPosition pos = hrPositionService.updatePosition(id, request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(pos)));
    }

    private PositionResponse toResponse(HrPosition domain) {
        return PositionResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
