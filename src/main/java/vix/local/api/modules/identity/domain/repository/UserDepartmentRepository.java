package vix.local.api.modules.identity.domain.repository;

import vix.local.api.modules.identity.domain.model.UserDepartment;
import vix.local.api.modules.identity.domain.model.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserDepartmentRepository {
    List<UserDepartment> findByUserId(UUID userId);

    UserDepartment save(UserDepartment userDepartment);

    // Upsert: update if exists for this user+dept pair, else create
    UserDepartment upsert(UUID userId, UUID departmentId, UserRole role, boolean isPrimary);

    void deleteByUserId(UUID userId);
}
