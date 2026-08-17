package vix.local.api.modules.permission.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.permission.api.v1.dto.request.AssignUsersRequest;
import vix.local.api.modules.permission.api.v1.dto.request.RoleGroupRequest;
import vix.local.api.modules.permission.api.v1.dto.response.PermissionMetadataResponse;
import vix.local.api.modules.permission.api.v1.dto.response.RoleGroupResponse;
import vix.local.api.modules.permission.application.service.PermissionApplicationService;
import vix.local.api.shared.dto.ApiResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Quản lý quyền và phân quyền trong phòng ban")
public class PermissionController {

    private final PermissionApplicationService permissionService;

    @GetMapping("/departments/{deptCode}/screens")
    @Operation(
            summary = "Lấy danh sách màn hình của phòng ban",
            description = "Trả về danh sách màn hình (resource) và các action được phép của phòng ban. " +
                    "HR dùng để lấy 'menu chọn' khi tạo hoặc chỉnh sửa role_group cho phòng ban đó.")
    public ResponseEntity<ApiResponse<List<PermissionMetadataResponse>>> getDepartmentScreens(
            @PathVariable String deptCode) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getDepartmentScreens(deptCode)));
    }

    @GetMapping("/metadata")
    @Operation(summary = "Lấy metadata phân quyền", description = "Lấy danh sách các tính năng và thao tác được hỗ trợ để hiển thị trên giao diện")
    public ResponseEntity<ApiResponse<List<PermissionMetadataResponse>>> getMetadata() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getPermissionMetadata()));
    }

    @GetMapping("/role-groups")
    @Operation(summary = "Lấy danh sách nhóm quyền", description = "Lấy các nhóm quyền trong phòng ban theo mã phòng ban")
    public ResponseEntity<ApiResponse<List<RoleGroupResponse>>> getRoleGroups(@RequestParam String deptCodeName) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getRoleGroupsByCodeName(deptCodeName)));
    }

    @PostMapping("/role-groups")
    @Operation(summary = "Tạo nhóm quyền mới", description = "Tạo nhóm quyền mới và phân quyền cho nhóm đó")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> createRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @Valid @RequestBody RoleGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.createRoleGroup(deptId, request)));
    }

    @PutMapping("/role-groups/{codeName}")
    @Operation(summary = "Cập nhật nhóm quyền", description = "Cập nhật thông tin và quyền hạn của một nhóm")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> updateRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable String codeName,
            @Valid @RequestBody RoleGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.updateRoleGroup(deptId, codeName, request)));
    }

    @DeleteMapping("/role-groups/{codeName}")
    @Operation(summary = "Xóa nhóm quyền", description = "Xóa nhóm quyền (nếu chưa gán cho ai)")
    public ResponseEntity<ApiResponse<Void>> deleteRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable String codeName) {
        permissionService.deleteRoleGroup(deptId, codeName);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/role-groups/{codeName}/assign-users")
    @Operation(summary = "Gán nhân viên vào nhóm quyền", description = "Cập nhật danh sách nhân viên thuộc nhóm quyền này")
    public ResponseEntity<ApiResponse<Void>> assignUsersToRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable String codeName,
            @Valid @RequestBody AssignUsersRequest request) {
        permissionService.assignUsersToRoleGroup(deptId, codeName, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/my-permissions")
    @Operation(summary = "Lấy quyền của user", description = "Lấy danh sách quyền của user trong phòng ban hiện tại")
    public ResponseEntity<ApiResponse<List<vix.local.api.modules.permission.api.v1.dto.request.PermissionDto>>> getMyPermissions(
            @RequestHeader("X-Department-Id") UUID deptId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getMyPermissions(deptId)));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Lấy quyền của một nhân viên", description = "Lấy danh sách quyền của một nhân viên cụ thể")
    public ResponseEntity<ApiResponse<List<vix.local.api.modules.permission.api.v1.dto.request.PermissionDto>>> getUserPermissions(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getUserPermissions(deptId, userId)));
    }

    @PostMapping("/users/{userId}")
    @Operation(summary = "Lưu quyền cho nhân viên", description = "Cập nhật (tạo role group riêng) quyền cho nhân viên")
    public ResponseEntity<ApiResponse<Void>> saveUserPermissions(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable UUID userId,
            @RequestBody List<vix.local.api.modules.permission.api.v1.dto.request.PermissionDto> permissions) {
        permissionService.saveUserPermissions(deptId, userId, permissions);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
