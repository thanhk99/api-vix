package vix.local.api.modules.audit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.audit.domain.model.AuditLog;
import vix.local.api.modules.audit.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditApplicationService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void logAsync(AuditLog logData) {
        try {
            auditLogRepository.save(logData);
            log.debug("Audit log saved successfully asynchronously");
        } catch (Exception e) {
            log.error("Failed to save audit log asynchronously", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByDepartment(UUID departmentId) {
        return auditLogRepository.findByDepartmentId(departmentId);
    }

}
