package vix.local.api.modules.permission.domain.repository;

import vix.local.api.modules.permission.domain.model.UserRoleGroup;

import java.util.List;
import java.util.UUID;

public interface UserRoleGroupRepository {
    List<UserRoleGroup> saveAll(List<UserRoleGroup> userRoleGroups);
    List<UserRoleGroup> findByUserIdAndDeptId(UUID userId, UUID deptId);
    List<UserRoleGroup> findByRoleGroupId(UUID roleGroupId);
    void deleteByRoleGroupId(UUID roleGroupId);
    boolean existsByRoleGroupId(UUID roleGroupId);
}
