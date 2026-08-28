package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PartnerDocument {
    private UUID id;
    private UUID partnerId;
    private String name;
    private String mimeType;
    private Long size;
    private String storagePath;
    private String uploadedBy;
    private LocalDateTime createdAt;
}
