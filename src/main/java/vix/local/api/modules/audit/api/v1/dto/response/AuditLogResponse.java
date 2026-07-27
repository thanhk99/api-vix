package vix.local.api.modules.audit.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private String action;
    private String module;
    private String description;
    private String performedBy;

    private UUID departmentId;
    private String ipAddress;
    private LocalDateTime timestamp;
}
