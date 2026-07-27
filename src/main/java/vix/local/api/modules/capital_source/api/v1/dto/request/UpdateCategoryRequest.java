package vix.local.api.modules.capital_source.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

    @NotBlank(message = "Tên danh mục không được rỗng")
    private String name;

    private String description;
}
