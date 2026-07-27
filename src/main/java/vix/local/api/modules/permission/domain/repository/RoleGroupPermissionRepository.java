package vix.local.api.modules.permission.domain.repository;

import vix.local.api.modules.permission.domain.model.RoleGroupPermission;

import java.util.List;
import java.util.UUID;

public interface RoleGroupPermissionRepository {
    List<RoleGroupPermission> saveAll(List<RoleGroupPermission> permissions);
    List<RoleGroupPermission> findByRoleGroupId(UUID roleGroupId);
    void deleteByRoleGroupId(UUID roleGroupId);
}
