package vix.local.api.modules.hr.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.hr.api.v1.dto.request.CreateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.response.DepartmentResponse;
import vix.local.api.modules.hr.application.service.HrDepartmentApplicationService;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.shared.dto.ApiResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/hr/departments")
@RequiredArgsConstructor
@Tag(name = "HR Department Management", description = "Quản lý phòng ban")
public class HrDepartmentController {

    private final HrDepartmentApplicationService hrDepartmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Lấy danh sách phòng ban")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> list = hrDepartmentService.getAllDepartments().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Lấy chi tiết phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(hrDepartmentService.getDepartmentById(id))));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Tạo phòng ban mới")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        HrDepartment dept = hrDepartmentService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Cập nhật thông tin phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        HrDepartment dept = hrDepartmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Vô hiệu hóa phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> deactivateDepartment(@PathVariable UUID id) {
        HrDepartment dept = hrDepartmentService.deactivateDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    private DepartmentResponse toResponse(HrDepartment domain) {
        return DepartmentResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .managerId(domain.getManagerId())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
