package vix.local.api.modules.hr.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.api.v1.dto.request.CreateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateDepartmentRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrDepartmentApplicationService {

    private final HrDepartmentRepository hrDepartmentRepository;

    @Transactional(readOnly = true)
    public List<HrDepartment> getAllDepartments() {
        return hrDepartmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public HrDepartment getDepartmentById(UUID id) {
        return hrDepartmentRepository.findById(id)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy phòng ban"));
    }

    @Transactional
    public HrDepartment createDepartment(CreateDepartmentRequest request) {
        if (hrDepartmentRepository.existsByCode(request.getCode())) {
            throw HrException.badRequest("Mã phòng ban đã tồn tại");
        }

        HrDepartment department = HrDepartment.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .status("ACTIVE")
                .managerId(request.getManagerId())
                .build();

        return hrDepartmentRepository.save(department);
    }

    @Transactional
    public HrDepartment updateDepartment(UUID id, UpdateDepartmentRequest request) {
        HrDepartment department = getDepartmentById(id);

        if (!department.getCode().equals(request.getCode()) && hrDepartmentRepository.existsByCode(request.getCode())) {
            throw HrException.badRequest("Mã phòng ban đã tồn tại");
        }

        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
        department.setManagerId(request.getManagerId());

        return hrDepartmentRepository.save(department);
    }

    @Transactional
    public HrDepartment deactivateDepartment(UUID id) {
        HrDepartment department = getDepartmentById(id);
        department.deactivate();
        return hrDepartmentRepository.save(department);
    }
}
