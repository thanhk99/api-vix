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

    @GetMapping("/metadata")
    @Operation(summary = "Lấy metadata phân quyền", description = "Lấy danh sách các tính năng và thao tác được hỗ trợ để hiển thị trên giao diện")
    public ResponseEntity<ApiResponse<List<PermissionMetadataResponse>>> getMetadata() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getPermissionMetadata()));
    }

    @GetMapping("/role-groups")
    @Operation(summary = "Lấy danh sách nhóm quyền", description = "Lấy các nhóm quyền trong phòng ban hiện tại")
    public ResponseEntity<ApiResponse<List<RoleGroupResponse>>> getRoleGroups(@RequestHeader("X-Department-Id") UUID deptId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getRoleGroups(deptId)));
    }

    @PostMapping("/role-groups")
    @Operation(summary = "Tạo nhóm quyền mới", description = "Tạo nhóm quyền mới và phân quyền cho nhóm đó")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> createRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @Valid @RequestBody RoleGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.createRoleGroup(deptId, request)));
    }

    @PutMapping("/role-groups/{id}")
    @Operation(summary = "Cập nhật nhóm quyền", description = "Cập nhật thông tin và quyền hạn của một nhóm")
    public ResponseEntity<ApiResponse<RoleGroupResponse>> updateRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable UUID id,
            @Valid @RequestBody RoleGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.updateRoleGroup(deptId, id, request)));
    }

    @DeleteMapping("/role-groups/{id}")
    @Operation(summary = "Xóa nhóm quyền", description = "Xóa nhóm quyền (nếu chưa gán cho ai)")
    public ResponseEntity<ApiResponse<Void>> deleteRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable UUID id) {
        permissionService.deleteRoleGroup(deptId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/role-groups/{id}/assign-users")
    @Operation(summary = "Gán nhân viên vào nhóm quyền", description = "Cập nhật danh sách nhân viên thuộc nhóm quyền này")
    public ResponseEntity<ApiResponse<Void>> assignUsersToRoleGroup(
            @RequestHeader("X-Department-Id") UUID deptId,
            @PathVariable UUID id,
            @Valid @RequestBody AssignUsersRequest request) {
        permissionService.assignUsersToRoleGroup(deptId, id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
