package vix.local.api.modules.hr.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vix.local.api.modules.hr.domain.model.HrUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HrUserRepository {
    Optional<HrUser> findById(UUID id);
    Optional<HrUser> findByEmail(String email);
    Optional<HrUser> findByEmployeeCode(String employeeCode);
    List<HrUser> findAll();
    List<HrUser> findByDepartmentId(UUID departmentId);
    List<HrUser> findByStatus(String status);
    Page<HrUser> findAllPaged(Pageable pageable);
    Page<HrUser> findByDepartmentIdPaged(UUID departmentId, Pageable pageable);
    Page<HrUser> searchByKeyword(String keyword, Pageable pageable);
    long countByDepartmentId(UUID departmentId);
    HrUser save(HrUser user);
}
