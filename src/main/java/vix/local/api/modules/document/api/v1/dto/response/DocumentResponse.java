package vix.local.api.modules.document.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {
    private UUID id;
    private String name;
    private String mimeType;
    private Long size;
    private String url;
    private UUID companyId;
    private UUID departmentId;
    private String uploadedBy;
    private LocalDateTime createdAt;
}
