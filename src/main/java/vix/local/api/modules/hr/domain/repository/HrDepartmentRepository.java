package vix.local.api.modules.hr.domain.repository;

import vix.local.api.modules.hr.domain.model.HrDepartment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HrDepartmentRepository {
    Optional<HrDepartment> findById(UUID id);
    Optional<HrDepartment> findByCode(String code);
    List<HrDepartment> findAll();
    List<HrDepartment> findAllById(List<UUID> ids);
    List<HrDepartment> findByStatus(String status);
    boolean existsByCode(String code);
    HrDepartment save(HrDepartment department);
}
