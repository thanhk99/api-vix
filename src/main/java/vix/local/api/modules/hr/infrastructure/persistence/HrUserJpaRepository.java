package vix.local.api.modules.hr.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HrUserJpaRepository extends JpaRepository<HrUserEntity, UUID> {
    Optional<HrUserEntity> findByEmail(String email);
    Optional<HrUserEntity> findByEmployeeCode(String employeeCode);
    List<HrUserEntity> findByDepartmentId(UUID departmentId);
    List<HrUserEntity> findByStatus(String status);
    Page<HrUserEntity> findByDepartmentId(UUID departmentId, Pageable pageable);
    long countByDepartmentId(UUID departmentId);

    @Query("SELECT u FROM HrUserEntity u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<HrUserEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
