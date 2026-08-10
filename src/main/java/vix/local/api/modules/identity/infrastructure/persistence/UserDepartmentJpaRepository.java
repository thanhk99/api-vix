package vix.local.api.modules.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDepartmentJpaRepository extends JpaRepository<UserDepartmentEntity, UUID> {
    List<UserDepartmentEntity> findByUserId(UUID userId);

    Optional<UserDepartmentEntity> findByUserIdAndDepartmentId(UUID userId, UUID departmentId);

    Optional<UserDepartmentEntity> findByDepartmentIdAndRole(UUID departmentId, vix.local.api.modules.identity.domain.model.UserRole role);

    void deleteByUserId(UUID userId);
}
