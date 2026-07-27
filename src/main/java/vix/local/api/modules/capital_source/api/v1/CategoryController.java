package vix.local.api.modules.capital_source.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.capital_source.api.v1.dto.request.CreateCategoryRequest;
import vix.local.api.modules.capital_source.api.v1.dto.request.UpdateCategoryRequest;
import vix.local.api.modules.capital_source.api.v1.dto.response.CategoryResponse;
import vix.local.api.modules.capital_source.application.mapper.CategoryMapper;
import vix.local.api.modules.capital_source.application.service.CategoryApplicationService;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;
import vix.local.api.shared.dto.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Quản lý danh mục dùng chung. Gồm 4 nhóm bắt buộc: BANK (Ngân hàng), LIMIT_TYPE (Loại hạn mức), ASSET_TYPE (Loại tài sản), LOAN_PURPOSE (Mục đích vay)")
public class CategoryController {

    private final CategoryApplicationService categoryService;
    private final CategoryMapper categoryMapper;

    @Operation(
        summary = "Lấy danh sách danh mục theo nhóm",
        description = "Truyền group: BANK | LIMIT_TYPE | ASSET_TYPE | LOAN_PURPOSE (4 nhóm bắt buộc của hệ thống)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getByGroup(
            @RequestParam CategoryGroup group) {
        List<CategoryResponse> responses = categoryService.getCategoriesByGroup(group)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "Lấy chi tiết danh mục theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @PathVariable UUID id) {
        CategoryResponse response = categoryMapper.toResponse(categoryService.getCategoryById(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "Tạo mới danh mục",
        description = "Mã (code) phải duy nhất trong cùng nhóm (group). Người tạo lấy từ JWT token."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request,
            Authentication auth) {
        String createdBy = auth.getName();
        CategoryResponse response = categoryMapper.toResponse(
                categoryService.createCategory(request, createdBy));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "Cập nhật thông tin danh mục",
        description = "Chỉ cho phép cập nhật tên (name) và mô tả (description). Không thể đổi nhóm (group)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryMapper.toResponse(
                categoryService.updateCategory(id, request));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "Ngừng sử dụng danh mục (Soft delete)",
        description = "Không xóa cứng. Chuyển trạng thái sang INACTIVE. Dữ liệu nghiệp vụ hiện có vẫn giữ nguyên."
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CategoryResponse>> deactivate(
            @PathVariable UUID id) {
        CategoryResponse response = categoryMapper.toResponse(
                categoryService.deactivateCategory(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "Kích hoạt lại danh mục",
        description = "Chuyển danh mục từ INACTIVE sang ACTIVE."
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CategoryResponse>> activate(
            @PathVariable UUID id) {
        CategoryResponse response = categoryMapper.toResponse(
                categoryService.activateCategory(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
