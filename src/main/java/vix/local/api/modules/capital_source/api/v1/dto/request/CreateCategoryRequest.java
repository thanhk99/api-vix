package vix.local.api.modules.capital_source.api.v1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Mã danh mục không được rỗng")
    private String code;

    @NotBlank(message = "Tên danh mục không được rỗng")
    private String name;

    private String description;

    @NotNull(message = "Nhóm danh mục không được null (BANK | LIMIT_TYPE | ASSET_TYPE | LOAN_PURPOSE)")
    private CategoryGroup group;
}
