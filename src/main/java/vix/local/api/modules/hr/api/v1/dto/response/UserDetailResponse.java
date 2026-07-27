package vix.local.api.modules.hr.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDetailResponse {
    private UUID id;
    private UUID companyId;
    private String email;
    private String fullName;
    private String status;
    private LocalDateTime createdAt;
}
