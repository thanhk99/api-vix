package vix.local.api.modules.capital_source.domain.repository;

import vix.local.api.modules.capital_source.domain.model.Category;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface thuần — không phụ thuộc Spring/JPA
 */
public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    Optional<Category> findByCodeAndGroup(String code, CategoryGroup group);

    List<Category> findAllByGroup(CategoryGroup group);

    boolean existsByCodeAndGroup(String code, CategoryGroup group);
}
