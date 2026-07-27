package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;
import vix.local.api.modules.capital_source.domain.model.CategoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private CategoryGroup group;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
}
