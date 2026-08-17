package vix.local.api.modules.permission.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.audit.application.service.AuditApplicationService;
import vix.local.api.modules.permission.application.service.DepartmentPermissionInitService.ScreenPermission;
import vix.local.api.modules.audit.domain.model.AuditLog;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.permission.api.v1.dto.request.AssignUsersRequest;
import vix.local.api.modules.permission.api.v1.dto.request.PermissionDto;
import vix.local.api.modules.permission.api.v1.dto.request.RoleGroupRequest;
import vix.local.api.modules.permission.api.v1.dto.response.PermissionMetadataResponse;
import vix.local.api.modules.permission.api.v1.dto.response.RoleGroupResponse;
import vix.local.api.modules.permission.domain.exception.PermissionException;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.RoleGroup;
import vix.local.api.modules.permission.domain.model.RoleGroupPermission;
import vix.local.api.modules.permission.domain.repository.RoleGroupPermissionRepository;
import vix.local.api.modules.permission.domain.repository.RoleGroupRepository;
import vix.local.api.modules.identity.domain.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

    private final RoleGroupRepository roleGroupRepository;
    private final RoleGroupPermissionRepository roleGroupPermissionRepository;
    private final UserRepository userRepository;
    private final AuditApplicationService auditService;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final DepartmentPermissionInitService departmentPermissionInitService;

    @Transactional(readOnly = true)
    public List<PermissionMetadataResponse> getPermissionMetadata() {
        return Arrays.stream(ResourceCode.values())
                .map(resource -> new PermissionMetadataResponse(resource, resource.getAllowedActions()))
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách màn hình (screens) và actions được phép của một phòng ban.
     * Frontend dùng API này để biết phòng ban có những màn hình nào
     * và trên mỗi màn hình có thể gán những action nào cho role_group.
     */
    @Transactional(readOnly = true)
    public List<PermissionMetadataResponse> getDepartmentScreens(String deptCode) {
        List<ScreenPermission> screens = departmentPermissionInitService.getScreensForDept(deptCode);
        if (screens.isEmpty()) {
            throw new PermissionException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Không tìm thấy cấu hình màn hình cho phòng ban: " + deptCode);
        }
        return screens.stream()
                .map(s -> new PermissionMetadataResponse(s.getResource(), s.getAllowedActions()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoleGroupResponse> getRoleGroups(UUID deptId) {
        return roleGroupRepository.findByDeptId(deptId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoleGroupResponse> getRoleGroupsByCodeName(String deptCodeName) {
        // Tìm department theo codeName để lấy ID
        Optional<HrDepartment> deptOpt = hrDepartmentRepository.findByCode(deptCodeName);
        if (deptOpt.isEmpty()) {
            throw new PermissionException(HttpStatus.NOT_FOUND,
                    "Department with code name '" + deptCodeName + "' not found");
        }

        UUID deptId = deptOpt.get().getId();
        return roleGroupRepository.findByDeptId(deptId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleGroupResponse createRoleGroup(UUID deptId, RoleGroupRequest request) {
        if (roleGroupRepository.existsByNameAndDeptId(request.getName(), deptId)) {
            throw PermissionException.roleGroupNameExists(request.getName());
        }

        UUID createdBy = getCurrentUserId();

        RoleGroup roleGroup = RoleGroup.builder()
                .deptId(deptId)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.isActive())
                .createdBy(createdBy)
                .build();
        roleGroup = roleGroupRepository.save(roleGroup);

        savePermissions(roleGroup.getId(), request.getPermissions());

        logAction(deptId, "CREATE_ROLE_GROUP", "Tạo nhóm quyền mới: " + roleGroup.getName());
        return toResponse(roleGroup);
    }

    @Transactional
    public RoleGroupResponse updateRoleGroup(UUID deptId, String codeName, RoleGroupRequest request) {
        // Tìm role group theo codeName thay vì ID
        RoleGroup roleGroup = roleGroupRepository.findByCodeNameAndDeptId(codeName, deptId)
                .orElseThrow(() -> PermissionException
                        .roleGroupNotFound("Role group with codeName " + codeName + " not found"));

        if (!roleGroup.getDeptId().equals(deptId)) {
            throw PermissionException.unauthorizedAction("Nhóm quyền này không thuộc phòng ban của bạn");
        }

        if (!roleGroup.getName().equals(request.getName()) &&
                roleGroupRepository.existsByNameAndDeptId(request.getName(), deptId)) {
            throw PermissionException.roleGroupNameExists(request.getName());
        }

        roleGroup.setName(request.getName());
        roleGroup.setDescription(request.getDescription());
        roleGroup.setActive(request.isActive());
        roleGroup = roleGroupRepository.save(roleGroup);

        savePermissions(roleGroup.getId(), request.getPermissions());

        logAction(deptId, "UPDATE_ROLE_GROUP", "Cập nhật nhóm quyền: " + roleGroup.getName());
        return toResponse(roleGroup);
    }

    @Transactional
    public void deleteRoleGroup(UUID deptId, String codeName) {
        // Tìm role group theo codeName thay vì ID
        RoleGroup roleGroup = roleGroupRepository.findByCodeNameAndDeptId(codeName, deptId)
                .orElseThrow(() -> PermissionException
                        .roleGroupNotFound("Role group with codeName " + codeName + " not found"));

        if (!roleGroup.getDeptId().equals(deptId)) {
            throw PermissionException.unauthorizedAction("Nhóm quyền này không thuộc phòng ban của bạn");
        }

        if (userRepository.findAll().stream().anyMatch(u -> roleGroup.getId().equals(u.getRoleGroupId()))) {
            throw PermissionException.unauthorizedAction("Không thể xóa nhóm quyền đang có nhân viên");
        }

        roleGroupPermissionRepository.deleteByRoleGroupId(roleGroup.getId());
        roleGroupRepository.deleteById(roleGroup.getId());

        logAction(deptId, "DELETE_ROLE_GROUP", "Xóa nhóm quyền: " + roleGroup.getName());
    }

    @Transactional
    public void assignUsersToRoleGroup(UUID deptId, String codeName, AssignUsersRequest request) {
        // Tìm role group theo codeName thay vì ID
        RoleGroup roleGroup = roleGroupRepository.findByCodeNameAndDeptId(codeName, deptId)
                .orElseThrow(() -> PermissionException
                        .roleGroupNotFound("Role group with codeName " + codeName + " not found"));

        if (!roleGroup.getDeptId().equals(deptId)) {
            throw PermissionException.unauthorizedAction("Nhóm quyền này không thuộc phòng ban của bạn");
        }

        List<UUID> newUsers = request.getUserIds();

        // Clear old users assigned to this role group
        userRepository.findAll().stream()
                .filter(u -> roleGroup.getId().equals(u.getRoleGroupId()))
                .forEach(u -> {
                    u.setRoleGroupId(null);
                    userRepository.save(u);
                });

        if (!newUsers.isEmpty()) {
            for (UUID userId : newUsers) {
                User user = userRepository.findById(userId).orElseThrow(() -> new PermissionException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found: " + userId));
                if (!deptId.equals(user.getDepartmentId())) {
                    throw PermissionException.unauthorizedAction("Nhân viên " + userId + " không thuộc phòng ban này");
                }
                user.setRoleGroupId(roleGroup.getId());
                userRepository.save(user);
            }
        }

        logAction(deptId, "ASSIGN_ROLE_GROUP", "Gán/Cập nhật nhân viên cho nhóm quyền: " + roleGroup.getName());
    }

    private void savePermissions(UUID roleGroupId, List<PermissionDto> permissions) {
        roleGroupPermissionRepository.deleteByRoleGroupId(roleGroupId);
        if (permissions != null && !permissions.isEmpty()) {
            List<RoleGroupPermission> entities = permissions.stream()
                    .map(p -> RoleGroupPermission.builder()
                            .roleGroupId(roleGroupId)
                            .resource(p.getResource())
                            .actions(p.getActions())
                            .build())
                    .collect(Collectors.toList());
            roleGroupPermissionRepository.saveAll(entities);
        }
    }

    private RoleGroupResponse toResponse(RoleGroup roleGroup) {
        RoleGroupResponse res = new RoleGroupResponse();
        res.setId(roleGroup.getId());
        res.setDeptId(roleGroup.getDeptId());
        res.setName(roleGroup.getName());
        res.setDescription(roleGroup.getDescription());
        res.setActive(roleGroup.isActive());
        res.setCreatedAt(roleGroup.getCreatedAt());
        res.setUpdatedAt(roleGroup.getUpdatedAt());

        // Thêm codeName vào response
        res.setCodeName(roleGroup.getCodeName());

        List<PermissionDto> dtos = roleGroupPermissionRepository.findByRoleGroupId(roleGroup.getId()).stream()
                .map(p -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setResource(p.getResource());
                    dto.setActions(p.getActions());
                    return dto;
                }).collect(Collectors.toList());
        res.setPermissions(dtos);
        return res;
    }

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).map(u -> u.getId()).orElse(null);
    }

    private void logAction(UUID deptId, String action, String description) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AuditLog logData = AuditLog.builder()
                .action(action)
                .module("PermissionApplicationService")
                .description(description)
                .performedBy(email)
                .departmentId(deptId)
                .build();
        auditService.logAsync(logData);
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> getMyPermissions(UUID deptId) {
        UUID userId = getCurrentUserId();
        if (userId == null) return List.of();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRoleGroupId() == null || !deptId.equals(user.getDepartmentId())) {
            return List.of();
        }

        List<PermissionDto> allPermissions = new java.util.ArrayList<>();
        List<RoleGroupPermission> perms = roleGroupPermissionRepository.findByRoleGroupId(user.getRoleGroupId());
        for (RoleGroupPermission p : perms) {
            PermissionDto dto = new PermissionDto();
            dto.setResource(p.getResource());
            dto.setActions(p.getActions());
            allPermissions.add(dto);
        }
        
        return allPermissions;
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> getUserPermissions(UUID deptId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PermissionException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (!deptId.equals(user.getDepartmentId())) {
            throw PermissionException.unauthorizedAction("Nhân viên không thuộc phòng ban này");
        }
        
        if (user.getRoleGroupId() == null) {
            return List.of();
        }
        
        return roleGroupPermissionRepository.findByRoleGroupId(user.getRoleGroupId()).stream()
                .map(p -> {
                    PermissionDto dto = new PermissionDto();
                    dto.setResource(p.getResource());
                    dto.setActions(p.getActions());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void saveUserPermissions(UUID deptId, UUID userId, List<PermissionDto> permissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PermissionException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (!deptId.equals(user.getDepartmentId())) {
            throw PermissionException.unauthorizedAction("Nhân viên không thuộc phòng ban này");
        }
        
        String customCodeName = "CUSTOM_ROLE_USER_" + userId.toString().replace("-", "").toUpperCase();
        
        RoleGroup roleGroup = roleGroupRepository.findByCodeNameAndDeptId(customCodeName, deptId)
                .orElseGet(() -> {
                    RoleGroup rg = RoleGroup.builder()
                            .deptId(deptId)
                            .name("Quyền riêng cho " + user.getFullName())
                            .description("Nhóm quyền tạo tự động cho user " + user.getEmail())
                            .isActive(true)
                            .createdBy(getCurrentUserId())
                            .codeName(customCodeName)
                            .build();
                    return roleGroupRepository.save(rg);
                });
                
        // Lưu quyền cho role group này
        savePermissions(roleGroup.getId(), permissions);
        
        // Gán user vào role group nếu chưa gán
        if (!roleGroup.getId().equals(user.getRoleGroupId())) {
            user.setRoleGroupId(roleGroup.getId());
            userRepository.save(user);
        }
        
        logAction(deptId, "SAVE_USER_PERMISSIONS", "Cập nhật quyền riêng cho nhân viên: " + user.getEmail());
    }
}
