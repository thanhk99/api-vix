package vix.local.api.modules.capital_source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import vix.local.api.modules.capital_source.domain.model.CategoryGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByGroup(CategoryGroup group);

    Optional<CategoryEntity> findByCodeAndGroup(String code, CategoryGroup group);

    boolean existsByCodeAndGroup(String code, CategoryGroup group);
}
