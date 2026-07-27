package vix.local.api.modules.permission.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.permission.domain.model.UserRoleGroup;
import vix.local.api.modules.permission.domain.repository.UserRoleGroupRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRoleGroupRepositoryImpl implements UserRoleGroupRepository {

    private final UserRoleGroupJpaRepository jpaRepository;

    @Override
    public List<UserRoleGroup> saveAll(List<UserRoleGroup> userRoleGroups) {
        List<UserRoleGroupEntity> entities = userRoleGroups.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleGroup> findByUserIdAndDeptId(UUID userId, UUID deptId) {
        return jpaRepository.findByUserIdAndDeptId(userId, deptId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleGroup> findByRoleGroupId(UUID roleGroupId) {
        return jpaRepository.findByRoleGroupId(roleGroupId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByRoleGroupId(UUID roleGroupId) {
        jpaRepository.deleteByRoleGroupId(roleGroupId);
    }

    @Override
    public boolean existsByRoleGroupId(UUID roleGroupId) {
        return jpaRepository.existsByRoleGroupId(roleGroupId);
    }

    private UserRoleGroup toDomain(UserRoleGroupEntity entity) {
        if (entity == null) return null;
        return UserRoleGroup.builder()
                .userId(entity.getUserId())
                .roleGroupId(entity.getRoleGroupId())
                .deptId(entity.getDeptId())
                .assignedBy(entity.getAssignedBy())
                .assignedAt(entity.getAssignedAt())
                .build();
    }

    private UserRoleGroupEntity toEntity(UserRoleGroup domain) {
        if (domain == null) return null;
        return UserRoleGroupEntity.builder()
                .userId(domain.getUserId())
                .roleGroupId(domain.getRoleGroupId())
                .deptId(domain.getDeptId())
                .assignedBy(domain.getAssignedBy())
                .assignedAt(domain.getAssignedAt())
                .build();
    }
}
