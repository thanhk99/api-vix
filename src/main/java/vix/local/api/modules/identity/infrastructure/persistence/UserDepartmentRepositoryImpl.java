package vix.local.api.modules.identity.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.identity.domain.model.UserDepartment;
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.repository.UserDepartmentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserDepartmentRepositoryImpl implements UserDepartmentRepository {

    private final UserDepartmentJpaRepository jpaRepository;

    @Override
    public List<UserDepartment> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserDepartment save(UserDepartment userDepartment) {
        UserDepartmentEntity entity = toEntity(userDepartment);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDepartment upsert(UUID userId, UUID departmentId, UserRole role, boolean isPrimary) {
        UserDepartmentEntity entity = jpaRepository.findByUserIdAndDepartmentId(userId, departmentId)
                .orElse(UserDepartmentEntity.builder()
                        .userId(userId)
                        .departmentId(departmentId)
                        .build());
        entity.setRole(role);
        entity.setPrimary(isPrimary);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private UserDepartment toDomain(UserDepartmentEntity entity) {
        if (entity == null)
            return null;
        return UserDepartment.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .departmentId(entity.getDepartmentId())
                .role(entity.getRole())
                .isPrimary(entity.isPrimary())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserDepartmentEntity toEntity(UserDepartment domain) {
        if (domain == null)
            return null;
        return UserDepartmentEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .departmentId(domain.getDepartmentId())
                .role(domain.getRole())
                .isPrimary(domain.isPrimary())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
