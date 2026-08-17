package vix.local.api.shared.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vix.local.api.modules.audit.application.service.AuditApplicationService;
import vix.local.api.modules.audit.domain.model.AuditLog;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditApplicationService auditService;

    // Intercept all endpoints in API controllers
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && execution(* vix.local.api.modules..api..*(..))")
    public void apiControllerMethods() {
    }

    @AfterReturning(pointcut = "apiControllerMethods()")
    public void logAfter(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes == null)
                return;
            HttpServletRequest request = attributes.getRequest();

            String method = request.getMethod();
            String uri = request.getRequestURI();

            // Lấy thông tin user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = "anonymous";
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                email = authentication.getName();
            }

            // Trích xuất companyId, departmentId từ Header hoặc AuthPort
            // Ở một hệ thống thực tế, thông tin này có thể được nhúng vào JWT claim
            UUID companyId = extractUuidFromHeader(request, "X-Company-Id");
            UUID departmentId = extractUuidFromHeader(request, "X-Department-Id");

            if (companyId == null) {
                // Default UUID for system actions if not found
                companyId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
            if (departmentId == null) {
                departmentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }

            String action = method + " " + uri;
            String module = joinPoint.getTarget().getClass().getSimpleName();
            String signatureName = joinPoint.getSignature().getName();

            AuditLog logData = AuditLog.builder()
                    .action(action)
                    .module(module)
                    .description("Method executed: " + signatureName)
                    .performedBy(email)
                    .departmentId(departmentId)
                    .ipAddress(request.getRemoteAddr())
                    .build();

            auditService.logAsync(logData);
        } catch (Exception e) {
            // Ignore audit log failure
        }
    }

    private UUID extractUuidFromHeader(HttpServletRequest request, String headerName) {
        String headerVal = request.getHeader(headerName);
        if (headerVal != null && !headerVal.isEmpty()) {
            try {
                return UUID.fromString(headerVal);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
