package vix.local.api.modules.audit.domain.repository;

import vix.local.api.modules.audit.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog log);
    List<AuditLog> findByDepartmentId(UUID departmentId);
    List<AuditLog> findByPerformedBy(String performedBy);
}
