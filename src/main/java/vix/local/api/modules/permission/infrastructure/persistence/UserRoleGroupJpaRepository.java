package vix.local.api.modules.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserRoleGroupJpaRepository extends JpaRepository<UserRoleGroupEntity, UserRoleGroupId> {
    List<UserRoleGroupEntity> findByUserIdAndDeptId(UUID userId, UUID deptId);
    List<UserRoleGroupEntity> findByRoleGroupId(UUID roleGroupId);
    void deleteByRoleGroupId(UUID roleGroupId);
    boolean existsByRoleGroupId(UUID roleGroupId);
}
