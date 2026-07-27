package vix.local.api.modules.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleGroupJpaRepository extends JpaRepository<RoleGroupEntity, UUID> {
    List<RoleGroupEntity> findByDeptId(UUID deptId);
    Optional<RoleGroupEntity> findByNameAndDeptId(String name, UUID deptId);
    boolean existsByNameAndDeptId(String name, UUID deptId);
}
