package vix.local.api.modules.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.permission.infrastructure.entity.ModulePermissionEntity;
import java.util.List;
import java.util.UUID;

@Repository
public interface ModulePermissionRepository extends JpaRepository<ModulePermissionEntity, UUID> {

    List<ModulePermissionEntity> findByDepartmentId(UUID departmentId);

    List<ModulePermissionEntity> findByModuleName(String moduleName);
}