package vix.local.api.modules.capital_source.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.api.v1.dto.request.CreateCategoryRequest;
import vix.local.api.modules.capital_source.api.v1.dto.request.UpdateCategoryRequest;
import vix.local.api.modules.capital_source.domain.exception.CategoryException;
import vix.local.api.modules.capital_source.domain.model.Category;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;
import vix.local.api.modules.capital_source.domain.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService {

    private final CategoryRepository categoryRepository;

    /**
     * Tạo mới danh mục bên trong một nhóm bắt buộc.
     * Kiểm tra trùng mã trong cùng nhóm trước khi tạo.
     */
    @Transactional
    public Category createCategory(CreateCategoryRequest request, String createdBy) {
        if (categoryRepository.existsByCodeAndGroup(request.getCode(), request.getGroup())) {
            throw CategoryException.duplicateCode(request.getCode(), request.getGroup().name());
        }
        Category category = Category.createNew(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getGroup(),
                createdBy
        );
        return categoryRepository.save(category);
    }

    /**
     * Cập nhật tên và mô tả danh mục.
     */
    @Transactional
    public Category updateCategory(UUID id, UpdateCategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        category.update(request.getName(), request.getDescription());
        return categoryRepository.save(category);
    }

    /**
     * Ngừng sử dụng danh mục (Soft delete).
     * Không xóa cứng — chỉ chuyển trạng thái sang INACTIVE.
     */
    @Transactional
    public Category deactivateCategory(UUID id) {
        Category category = findCategoryOrThrow(id);
        try {
            category.deactivate();
        } catch (IllegalStateException e) {
            throw CategoryException.invalidState(e.getMessage());
        }
        return categoryRepository.save(category);
    }

    /**
     * Kích hoạt lại danh mục đã ngừng sử dụng.
     */
    @Transactional
    public Category activateCategory(UUID id) {
        Category category = findCategoryOrThrow(id);
        try {
            category.activate();
        } catch (IllegalStateException e) {
            throw CategoryException.invalidState(e.getMessage());
        }
        return categoryRepository.save(category);
    }

    /**
     * Lấy danh sách danh mục theo nhóm bắt buộc (BANK / LIMIT_TYPE / ASSET_TYPE / LOAN_PURPOSE).
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByGroup(CategoryGroup group) {
        return categoryRepository.findAllByGroup(group);
    }

    /**
     * Lấy chi tiết một danh mục theo ID.
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(UUID id) {
        return findCategoryOrThrow(id);
    }

    // ===================== Private helpers =====================

    private Category findCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> CategoryException.notFound(id.toString()));
    }
}
