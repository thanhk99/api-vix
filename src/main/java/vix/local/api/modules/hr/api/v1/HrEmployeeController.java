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
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.shared.dto.ApiResponse;
import vix.local.api.shared.dto.PagedResponse;

import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.domain.repository.HrPositionRepository;
import vix.local.api.modules.identity.domain.repository.UserDepartmentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/hr/employees")
@RequiredArgsConstructor
@Tag(name = "HR Employee Management", description = "Quản lý nhân sự toàn công ty")
public class HrEmployeeController {

    private final HrEmployeeApplicationService hrEmployeeService;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final HrPositionRepository hrPositionRepository;
    private final UserDepartmentRepository userDepartmentRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Tạo mới nhân viên", description = "Dành cho Super Admin / Admin cấp tài khoản")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        HrUser user = hrEmployeeService.createEmployee(request);
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DEPT_ADMIN')")
    @Operation(summary = "Lấy danh sách nhân viên", description = "Trả về danh sách phân trang (có thể lọc theo phòng ban, tìm kiếm)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeListItemResponse>>> searchEmployees(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HrUser> pagedResult = hrEmployeeService.searchEmployees(departmentId, keyword, pageable);

        List<EmployeeListItemResponse> content = pagedResult.getContent().stream()
                .map(this::toListItemResponse)
                .collect(Collectors.toList());

        PagedResponse<EmployeeListItemResponse> response = PagedResponse.<EmployeeListItemResponse>builder()
                .content(content)
                .pageNumber(pagedResult.getNumber())
                .pageSize(pagedResult.getSize())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .isLast(pagedResult.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DEPT_ADMIN')")
    @Operation(summary = "Lấy chi tiết nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getEmployeeById(@PathVariable UUID id) {
        HrUser user = hrEmployeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        HrUser user = hrEmployeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @PatchMapping("/{id}/transfer")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Chuyển phòng ban cho nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> transferDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody TransferDepartmentRequest request) {
        HrUser user = hrEmployeeService.transferDepartment(id, request.getNewDepartmentId());
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @PatchMapping("/{id}/terminate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Báo nghỉ việc nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> terminateEmployee(@PathVariable UUID id) {
        HrUser user = hrEmployeeService.terminateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Tạm nghỉ / Vô hiệu hóa nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> deactivateEmployee(@PathVariable UUID id) {
        HrUser user = hrEmployeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(toDetailResponse(user)));
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Reset mật khẩu nhân viên")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable UUID id,
            @Valid @RequestBody ResetPasswordRequest request) {
        hrEmployeeService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private EmployeeDetailResponse toDetailResponse(HrUser user) {
        String deptName = null;
        String deptCode = null;
        if (user.getDepartmentId() != null) {
            var deptOpt = hrDepartmentRepository.findById(user.getDepartmentId());
            if (deptOpt.isPresent()) {
                deptName = deptOpt.get().getName();
                deptCode = deptOpt.get().getCode();
            }
        }

        String posName = null;
        String posCode = null;
        if (user.getPositionId() != null) {
            var posOpt = hrPositionRepository.findById(user.getPositionId());
            if (posOpt.isPresent()) {
                posName = posOpt.get().getName();
                posCode = posOpt.get().getCode();
            }
        }

        vix.local.api.modules.identity.domain.model.UserRole role = null;
        var udList = userDepartmentRepository.findByUserId(user.getId());
        if (!udList.isEmpty()) {
            role = udList.get(0).getRole();
        }

        return EmployeeDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .employeeCode(user.getEmployeeCode())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .idCardNumber(user.getIdCardNumber())
                .idCardIssuedDate(user.getIdCardIssuedDate())
                .idCardIssuedPlace(user.getIdCardIssuedPlace())
                .departmentId(user.getDepartmentId())
                .departmentName(deptName)
                .departmentCode(deptCode)
                .positionId(user.getPositionId())
                .positionName(posName)
                .positionCode(posCode)
                .role(role)
                .joinDate(user.getJoinDate())
                .terminateDate(user.getTerminateDate())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private EmployeeListItemResponse toListItemResponse(HrUser user) {
        String deptName = null;
        String deptCode = null;
        if (user.getDepartmentId() != null) {
            var deptOpt = hrDepartmentRepository.findById(user.getDepartmentId());
            if (deptOpt.isPresent()) {
                deptName = deptOpt.get().getName();
                deptCode = deptOpt.get().getCode();
            }
        }

        String posName = null;
        String posCode = null;
        if (user.getPositionId() != null) {
            var posOpt = hrPositionRepository.findById(user.getPositionId());
            if (posOpt.isPresent()) {
                posName = posOpt.get().getName();
                posCode = posOpt.get().getCode();
            }
        }

        vix.local.api.modules.identity.domain.model.UserRole role = null;
        var udList = userDepartmentRepository.findByUserId(user.getId());
        if (!udList.isEmpty()) {
            role = udList.get(0).getRole();
        }

        return EmployeeListItemResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .employeeCode(user.getEmployeeCode())
                .departmentId(user.getDepartmentId())
                .departmentName(deptName)
                .departmentCode(deptCode)
                .positionId(user.getPositionId())
                .positionName(posName)
                .positionCode(posCode)
                .role(role)
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
