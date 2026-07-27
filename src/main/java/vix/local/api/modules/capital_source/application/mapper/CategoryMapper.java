package vix.local.api.modules.capital_source.application.mapper;

import org.springframework.stereotype.Component;
import vix.local.api.modules.capital_source.api.v1.dto.response.CategoryResponse;
import vix.local.api.modules.capital_source.domain.model.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .group(category.getGroup())
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
