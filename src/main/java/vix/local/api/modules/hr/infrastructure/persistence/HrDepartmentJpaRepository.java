package vix.local.api.modules.hr.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface HrDepartmentJpaRepository extends JpaRepository<HrDepartmentEntity, UUID> {
    Optional<HrDepartmentEntity> findByCode(String code);
    List<HrDepartmentEntity> findByStatus(String status);
    boolean existsByCode(String code);
}
