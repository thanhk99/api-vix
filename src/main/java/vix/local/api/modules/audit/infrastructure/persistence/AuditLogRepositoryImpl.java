package vix.local.api.modules.audit.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.audit.domain.model.AuditLog;
import vix.local.api.modules.audit.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLog save(AuditLog log) {
        AuditLogEntity entity = toEntity(log);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<AuditLog> findByDepartmentId(UUID departmentId) {
        return jpaRepository.findByDepartmentIdOrderByTimestampDesc(departmentId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }



    private AuditLog toDomain(AuditLogEntity entity) {
        if (entity == null) return null;
        return AuditLog.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .module(entity.getModule())
                .description(entity.getDescription())
                .performedBy(entity.getPerformedBy())

                .departmentId(entity.getDepartmentId())
                .ipAddress(entity.getIpAddress())
                .timestamp(entity.getTimestamp())
                .build();
    }

    private AuditLogEntity toEntity(AuditLog domain) {
        if (domain == null) return null;
        return AuditLogEntity.builder()
                .id(domain.getId())
                .action(domain.getAction())
                .module(domain.getModule())
                .description(domain.getDescription())
                .performedBy(domain.getPerformedBy())

                .departmentId(domain.getDepartmentId())
                .ipAddress(domain.getIpAddress())
                .timestamp(domain.getTimestamp())
                .build();
    }
}
