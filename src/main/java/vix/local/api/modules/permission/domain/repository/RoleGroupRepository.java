package vix.local.api.modules.permission.domain.repository;

import vix.local.api.modules.permission.domain.model.RoleGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleGroupRepository {
    RoleGroup save(RoleGroup roleGroup);
    Optional<RoleGroup> findById(UUID id);
    List<RoleGroup> findByDeptId(UUID deptId);
    boolean existsByNameAndDeptId(String name, UUID deptId);
    void deleteById(UUID id);

    // Thêm phương thức tìm theo codeName
    Optional<RoleGroup> findByCodeNameAndDeptId(String codeName, UUID deptId);
}
