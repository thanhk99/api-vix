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

        // DEPT_ADMIN has all permissions across their department
        if (roles.contains("ROLE_DEPT_ADMIN")) {
            return;
        }

        // schemaTarget check removed as it blocked MEMBER access in shared schema

        // Parse deptId from Context. 
        UUID deptId = getDeptIdFromRequest();
        if (deptId == null) {
            throw new AccessDeniedException("Không xác định được phòng ban của bạn");
        }

        if (user.getRoleGroupId() == null || !deptId.equals(user.getDepartmentId())) {
            throw new AccessDeniedException("Bạn chưa được phân vào nhóm quyền nào trong phòng ban này");
        }

        UUID groupId = user.getRoleGroupId();

        RoleGroupEntity roleGroup = roleGroupJpaRepository.findById(groupId)
                .orElseThrow(() -> new AccessDeniedException("Nhóm quyền không tồn tại"));

        if (!roleGroup.isActive()) {
            throw new AccessDeniedException("Nhóm quyền của bạn trong phòng ban này đã bị vô hiệu hóa");
        }

        boolean hasPermission = false;
        List<RoleGroupPermissionEntity> permissions = roleGroupPermissionJpaRepository.findByRoleGroupId(groupId);
        if (permissions.stream().anyMatch(p ->
                p.getResource() == requireDeptPermission.resource() &&
                p.getActions().contains(requireDeptPermission.action()))) {
            hasPermission = true;
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
                Object deptIdAttr = attributes.getRequest().getAttribute("X-Department-Id");
                if (deptIdAttr != null) {
                    return (UUID) deptIdAttr;
                }
            }
        } catch (Exception e) {}
        return null;
    }
}
