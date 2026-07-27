package vix.local.api.modules.hr.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HrPositionJpaRepository extends JpaRepository<HrPositionEntity, UUID> {
    Optional<HrPositionEntity> findByCode(String code);
    boolean existsByCode(String code);
}
