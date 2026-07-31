package vix.local.api.modules.permission.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vix.local.api.modules.permission.domain.model.ModulePermission;
import vix.local.api.modules.permission.infrastructure.entity.ModulePermissionEntity;
import vix.local.api.modules.permission.infrastructure.persistence.ModulePermissionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModulePermissionService {

    private final ModulePermissionRepository modulePermissionRepository;

    public List<ModulePermission> getAllModules() {
        return List.of(ModulePermission.values());
    }

    public List<ModulePermissionEntity> getModulePermissionsByDepartment(UUID departmentId) {
        return modulePermissionRepository.findByDepartmentId(departmentId);
    }

    public ModulePermissionEntity saveModulePermission(ModulePermissionEntity entity) {
        return modulePermissionRepository.save(entity);
    }

    public void deleteModulePermission(UUID id) {
        modulePermissionRepository.deleteById(id);
    }
}