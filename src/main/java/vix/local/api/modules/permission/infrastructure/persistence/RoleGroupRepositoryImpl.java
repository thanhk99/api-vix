package vix.local.api.modules.permission.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.permission.domain.model.RoleGroup;
import vix.local.api.modules.permission.domain.repository.RoleGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleGroupRepositoryImpl implements RoleGroupRepository {

    private final RoleGroupJpaRepository jpaRepository;

    @Override
    public RoleGroup save(RoleGroup roleGroup) {
        RoleGroupEntity entity = toEntity(roleGroup);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RoleGroup> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<RoleGroup> findByDeptId(UUID deptId) {
        return jpaRepository.findByDeptId(deptId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndDeptId(String name, UUID deptId) {
        return jpaRepository.existsByNameAndDeptId(name, deptId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private RoleGroup toDomain(RoleGroupEntity entity) {
        if (entity == null) return null;
        return RoleGroup.builder()
                .id(entity.getId())
                .deptId(entity.getDeptId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private RoleGroupEntity toEntity(RoleGroup domain) {
        if (domain == null) return null;
        return RoleGroupEntity.builder()
                .id(domain.getId())
                .deptId(domain.getDeptId())
                .name(domain.getName())
                .description(domain.getDescription())
                .isActive(domain.isActive())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
