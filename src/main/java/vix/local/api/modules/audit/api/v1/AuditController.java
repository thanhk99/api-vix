package vix.local.api.modules.audit.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.audit.api.v1.dto.response.AuditLogResponse;
import vix.local.api.modules.audit.application.service.AuditApplicationService;
import vix.local.api.modules.audit.domain.model.AuditLog;
import vix.local.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/audits")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Truy vấn lịch sử thao tác hệ thống")
public class AuditController {

    private final AuditApplicationService auditService;

    @GetMapping("/department")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DEPT_ADMIN')")
    @Operation(summary = "Lấy Audit Log theo phòng ban", description = "Lấy danh sách thao tác theo departmentId lấy từ Header")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByDepartment(@RequestHeader("X-Department-Id") UUID departmentId) {
        List<AuditLogResponse> logs = auditService.getLogsByDepartment(departmentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .module(log.getModule())
                .description(log.getDescription())
                .performedBy(log.getPerformedBy())
                .departmentId(log.getDepartmentId())
                .ipAddress(log.getIpAddress())
                .timestamp(log.getTimestamp())
                .build();
    }
}
