package vix.local.api.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vix.local.api.modules.permission.api.v1.dto.request.PermissionDto;
import vix.local.api.modules.permission.application.service.PermissionApplicationService;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Component("permissionGuard")
@RequiredArgsConstructor
public class PermissionGuard {

    private final PermissionApplicationService permissionService;

    public boolean has(String resourceStr, String actionStr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // DIRECTOR bypass toàn bộ - full access mọi phòng ban, không cần departmentId
        boolean isDirector = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR"));
        if (isDirector) {
            return true;
        }

        // DEPT_ADMIN bypass resource-level permissions (chỉ trong phòng ban của mình)
        boolean isFullAccess = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DEPT_ADMIN"));
        
        if (isFullAccess) {
            return true;
        }

        // Fetch department id from request attribute (injected by JwtAuthenticationFilter)
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return false;
        }
        
        HttpServletRequest request = attrs.getRequest();
        Object deptIdAttr = request.getAttribute("X-Department-Id");
        if (deptIdAttr == null) {
            return false;
        }

        UUID deptId = (UUID) deptIdAttr;
        ResourceCode resource;
        ActionCode action;
        try {
            resource = ResourceCode.valueOf(resourceStr);
            action = ActionCode.valueOf(actionStr);
        } catch (IllegalArgumentException e) {
            return false;
        }

        List<PermissionDto> myPermissions = permissionService.getMyPermissions(deptId);
        return myPermissions.stream()
                .filter(p -> p.getResource() == resource)
                .anyMatch(p -> p.getActions().contains(action));
    }
}
