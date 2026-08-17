package vix.local.api.modules.identity.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        return toDomain(jpaRepository.save(entity));
    }

    private User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .passwordHash(entity.getPasswordHash())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .departmentId(entity.getDepartmentId())
                .departmentRole(entity.getDepartmentRole())
                .roleGroupId(entity.getRoleGroupId())
                .build();
    }

    private UserEntity toEntity(User user) {
        if (user == null) return null;
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .passwordHash(user.getPasswordHash())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .departmentId(user.getDepartmentId())
                .departmentRole(user.getDepartmentRole())
                .roleGroupId(user.getRoleGroupId())
                .build();
    }
}
