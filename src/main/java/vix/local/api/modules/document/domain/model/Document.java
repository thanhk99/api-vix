package vix.local.api.modules.document.domain.model;

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
public class Document {
    private UUID id;
    private String name;
    private String mimeType;
    private Long size;
    private String storagePath;
    private UUID companyId;
    private UUID departmentId;
    private String uploadedBy;
    private LocalDateTime createdAt;
}
