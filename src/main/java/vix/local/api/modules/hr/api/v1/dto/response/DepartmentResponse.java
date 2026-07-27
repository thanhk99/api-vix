package vix.local.api.modules.hr.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DepartmentResponse {
    private UUID id;
    private String name;
    private String code;
    private UUID managerId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
