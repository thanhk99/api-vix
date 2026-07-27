package vix.local.api.modules.audit.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private UUID id;
    private String action;
    private String module;
    private String description;
    private String performedBy;

    private UUID departmentId;
    private String ipAddress;
    private LocalDateTime timestamp;
}
