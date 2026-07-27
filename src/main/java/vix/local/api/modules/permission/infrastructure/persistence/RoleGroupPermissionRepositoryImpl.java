package vix.local.api.modules.permission.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.permission.domain.model.RoleGroupPermission;
import vix.local.api.modules.permission.domain.repository.RoleGroupPermissionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleGroupPermissionRepositoryImpl implements RoleGroupPermissionRepository {

    private final RoleGroupPermissionJpaRepository jpaRepository;

    @Override
    public List<RoleGroupPermission> saveAll(List<RoleGroupPermission> permissions) {
        List<RoleGroupPermissionEntity> entities = permissions.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleGroupPermission> findByRoleGroupId(UUID roleGroupId) {
        return jpaRepository.findByRoleGroupId(roleGroupId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByRoleGroupId(UUID roleGroupId) {
        jpaRepository.deleteByRoleGroupId(roleGroupId);
    }

    private RoleGroupPermission toDomain(RoleGroupPermissionEntity entity) {
        if (entity == null) return null;
        return RoleGroupPermission.builder()
                .id(entity.getId())
                .roleGroupId(entity.getRoleGroupId())
                .resource(entity.getResource())
                .actions(entity.getActions())
                .build();
    }

    private RoleGroupPermissionEntity toEntity(RoleGroupPermission domain) {
        if (domain == null) return null;
        return RoleGroupPermissionEntity.builder()
                .id(domain.getId())
                .roleGroupId(domain.getRoleGroupId())
                .resource(domain.getResource())
                .actions(domain.getActions())
                .build();
    }
}
