package vix.local.api.modules.hr.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.hr.api.v1.dto.request.CreateEmployeeRequest;
import vix.local.api.modules.hr.api.v1.dto.request.ResetPasswordRequest;
import vix.local.api.modules.hr.api.v1.dto.request.TransferDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateEmployeeRequest;
import vix.local.api.modules.hr.api.v1.dto.response.EmployeeDetailResponse;
import vix.local.api.modules.hr.api.v1.dto.response.EmployeeListItemResponse;
import vix.local.api.modules.hr.application.service.HrEmployeeApplicationService;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import java.util.UUID;

@RestController
@RequestMapping("/v1/hr/employees")
@RequiredArgsConstructor
@Tag(name = "HR Employee Management", description = "Quản lý nhân sự toàn công ty")
public class HrEmployeeController {

    private final HrEmployeeApplicationService hrEmployeeService;

    @PostMapping
    @PreAuthorize("@permissionGuard.has('HR_USER', 'CREATE')")
    @Operation(summary = "Tạo mới nhân viên", description = "Dành cho Admin/Dept Admin tạo tài khoản")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.createEmployee(request)));
    }

    @GetMapping
    @PreAuthorize("@permissionGuard.has('HR_USER', 'VIEW')")
    @Operation(summary = "Lấy danh sách nhân viên", description = "Trả về danh sách phân trang (có thể lọc theo phòng ban, tìm kiếm)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeListItemResponse>>> searchEmployees(
            @RequestHeader(value = "X-Department-Id", required = false) UUID headerDeptId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (departmentId == null) {
            departmentId = headerDeptId;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeListItemResponse> pagedResult = hrEmployeeService.searchEmployees(departmentId, keyword, pageable);

        PagedResponse<EmployeeListItemResponse> response = PagedResponse.<EmployeeListItemResponse>builder()
                .content(pagedResult.getContent())
                .pageNumber(pagedResult.getNumber())
                .pageSize(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .isLast(pagedResult.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'VIEW')")
    @Operation(summary = "Lấy chi tiết nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.getEmployeeDetailById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'UPDATE')")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.updateEmployee(id, request)));
    }

    @PatchMapping("/{id}/transfer")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'APPROVE')")
    @Operation(summary = "Chuyển phòng ban cho nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> transferDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody TransferDepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.transferDepartment(id, request.getNewDepartmentId())));
    }

    @PatchMapping("/{id}/terminate")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'DELETE')")
    @Operation(summary = "Báo nghỉ việc nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> terminateEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.terminateEmployee(id)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'DELETE')")
    @Operation(summary = "Tạm nghỉ / Vô hiệu hóa nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> deactivateEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(hrEmployeeService.deactivateEmployee(id)));
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("@permissionGuard.has('HR_USER', 'UPDATE')")
    @Operation(summary = "Reset mật khẩu nhân viên")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable UUID id,
            @Valid @RequestBody ResetPasswordRequest request) {
        hrEmployeeService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
