package vix.local.api.modules.capital_source.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.capital_source.domain.model.Category;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;
import vix.local.api.modules.capital_source.domain.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = toEntity(category);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Category> findByCodeAndGroup(String code, CategoryGroup group) {
        return jpaRepository.findByCodeAndGroup(code, group).map(this::toDomain);
    }

    @Override
    public List<Category> findAllByGroup(CategoryGroup group) {
        return jpaRepository.findByGroup(group)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCodeAndGroup(String code, CategoryGroup group) {
        return jpaRepository.existsByCodeAndGroup(code, group);
    }

    // ===================== Mapping =====================

    private Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .group(entity.getGroup())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        return CategoryEntity.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .group(domain.getGroup())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
