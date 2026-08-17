package vix.local.api.modules.hr.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.hr.api.v1.dto.request.CreateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.request.SetManagerRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.response.DepartmentResponse;
import vix.local.api.modules.hr.application.service.HrDepartmentApplicationService;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.modules.hr.domain.repository.HrUserRepository;
import vix.local.api.shared.dto.ApiResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/hr/departments")
@RequiredArgsConstructor
@Tag(name = "HR Department Management", description = "Quản lý phòng ban")
public class HrDepartmentController {

    private final HrDepartmentApplicationService hrDepartmentService;
    private final HrUserRepository hrUserRepository;

    @GetMapping
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'VIEW')")
    @Operation(summary = "Lấy danh sách phòng ban")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        List<DepartmentResponse> list = hrDepartmentService.getAllDepartments().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'VIEW')")
    @Operation(summary = "Lấy chi tiết phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(hrDepartmentService.getDepartmentById(id))));
    }

    @PostMapping
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'CREATE')")
    @Operation(summary = "Tạo phòng ban mới")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        HrDepartment dept = hrDepartmentService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'UPDATE')")
    @Operation(summary = "Cập nhật thông tin phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        HrDepartment dept = hrDepartmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    @PatchMapping("/{id}/set-manager")
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'APPROVE')")
    @Operation(summary = "Thiết lập trưởng phòng cho phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> setManager(
            @PathVariable UUID id,
            @Valid @RequestBody SetManagerRequest request) {
        hrDepartmentService.setManager(id, request.getManagerId());
        return ResponseEntity.ok(ApiResponse.success(toResponse(hrDepartmentService.getDepartmentById(id))));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("@permissionGuard.has('HR_DEPARTMENT', 'DELETE')")
    @Operation(summary = "Vô hiệu hóa phòng ban")
    public ResponseEntity<ApiResponse<DepartmentResponse>> deactivateDepartment(@PathVariable UUID id) {
        HrDepartment dept = hrDepartmentService.deactivateDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(toResponse(dept)));
    }

    private DepartmentResponse toResponse(HrDepartment domain) {
        UUID managerId = null;
        String managerName = null;
        String managerCode = null;

        Optional<HrUser> managerUserOpt = hrUserRepository.findAll().stream()
            .filter(u -> domain.getId().equals(u.getDepartmentId()) && vix.local.api.modules.identity.domain.model.UserRole.DEPT_ADMIN == u.getDepartmentRole())
            .findFirst();

        if (managerUserOpt.isPresent()) {
            managerId = managerUserOpt.get().getId();
            managerName = managerUserOpt.get().getFullName();
            managerCode = managerUserOpt.get().getEmployeeCode();
        }

        return DepartmentResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .managerId(managerId)
                .managerName(managerName)
                .managerCode(managerCode)
                .description(domain.getDescription())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
