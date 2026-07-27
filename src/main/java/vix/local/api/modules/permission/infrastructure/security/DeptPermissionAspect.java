package vix.local.api.modules.permission.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.permission.infrastructure.persistence.RoleGroupEntity;
import vix.local.api.modules.permission.infrastructure.persistence.RoleGroupJpaRepository;
import vix.local.api.modules.permission.infrastructure.persistence.RoleGroupPermissionEntity;
import vix.local.api.modules.permission.infrastructure.persistence.RoleGroupPermissionJpaRepository;
import vix.local.api.modules.permission.infrastructure.persistence.UserRoleGroupEntity;
import vix.local.api.modules.permission.infrastructure.persistence.UserRoleGroupJpaRepository;
import vix.local.api.shared.tenant.TenantContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class DeptPermissionAspect {

    private final UserRepository userRepository;
    private final UserRoleGroupJpaRepository userRoleGroupJpaRepository;
    private final RoleGroupJpaRepository roleGroupJpaRepository;
    private final RoleGroupPermissionJpaRepository roleGroupPermissionJpaRepository;

    @Before("@annotation(requireDeptPermission)")
    public void checkPermission(JoinPoint joinPoint, RequireDeptPermission requireDeptPermission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        // parse roles from authentication
        List<String> roles = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // SUPER_ADMIN and ADMIN have all permissions across all departments
        if (roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN") || roles.contains("SUPER_ADMIN") || roles.contains("ADMIN")) {
            return;
        }

        String schemaTarget = TenantContext.getSchema();
        if (schemaTarget == null || schemaTarget.equals("shared")) {
            throw new AccessDeniedException("Không xác định được phòng ban hiện tại (schema)");
        }

        // Parse deptId from schemaTarget or Context. 
        // Note: For now, we need deptId. We can get it from headers or we can use another context.
        // Assuming TenantContext also has deptId, but if not we can extract it if we need to.
        // Since we don't have deptId directly in TenantContext currently, we might need it.
        // Let's assume we can get it from a RequestHeader via RequestContextHolder or we add it to TenantContext later.
        UUID deptId = getDeptIdFromRequest();
        if (deptId == null) {
            throw new AccessDeniedException("Thiếu X-Department-Id header để phân quyền");
        }

        List<UserRoleGroupEntity> userGroups = userRoleGroupJpaRepository.findByUserIdAndDeptId(user.getId(), deptId);
        if (userGroups.isEmpty()) {
            throw new AccessDeniedException("Bạn chưa được phân vào nhóm quyền nào trong phòng ban này");
        }

        Set<UUID> validGroupIds = userGroups.stream()
                .map(UserRoleGroupEntity::getRoleGroupId)
                .collect(Collectors.toSet());

        Set<UUID> activeGroupIds = new HashSet<>();
        for (UUID groupId : validGroupIds) {
            roleGroupJpaRepository.findById(groupId)
                    .filter(RoleGroupEntity::isActive)
                    .ifPresent(g -> activeGroupIds.add(groupId));
        }

        if (activeGroupIds.isEmpty()) {
            throw new AccessDeniedException("Các nhóm quyền của bạn trong phòng ban này đã bị vô hiệu hóa");
        }

        boolean hasPermission = false;
        for (UUID groupId : activeGroupIds) {
            List<RoleGroupPermissionEntity> permissions = roleGroupPermissionJpaRepository.findByRoleGroupId(groupId);
            if (permissions.stream().anyMatch(p ->
                    p.getResource() == requireDeptPermission.resource() &&
                    p.getActions().contains(requireDeptPermission.action()))) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này trong phòng ban hiện tại");
        }
    }

    private UUID getDeptIdFromRequest() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = 
                (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String deptIdHeader = attributes.getRequest().getHeader("X-Department-Id");
                if (deptIdHeader != null) {
                    return UUID.fromString(deptIdHeader);
                }
            }
        } catch (Exception e) {}
        return null;
    }
}
