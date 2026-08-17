package vix.local.api.modules.hr.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.api.v1.dto.request.CreateDepartmentRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateDepartmentRequest;
import vix.local.api.modules.identity.application.port.IdentityPort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrDepartmentApplicationService {

    private final HrDepartmentRepository hrDepartmentRepository;
    private final IdentityPort identityPort;
    private final vix.local.api.modules.hr.domain.repository.HrUserRepository hrUserRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

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
                .build();

        HrDepartment saved = hrDepartmentRepository.save(department);

        eventPublisher.publishEvent(new vix.local.api.shared.event.DepartmentCreatedEvent(this, saved.getId(), saved.getCode(), saved.getName()));
        return saved;
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

        return hrDepartmentRepository.save(department);
    }

    @Transactional
    public void setManager(UUID departmentId, UUID userId) {
        HrDepartment department = getDepartmentById(departmentId);
        vix.local.api.modules.hr.domain.model.HrUser user = hrUserRepository.findById(userId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));

        if (user.getDepartmentId() == null || !user.getDepartmentId().equals(department.getId())) {
            throw HrException.badRequest("Nhân viên không thuộc phòng ban này");
        }

        // 1. Hạ trưởng phòng cũ xuống MEMBER (nếu có)
        identityPort.demoteOldManager(department.getId(), userId);

        // 2. Set trưởng phòng mới
        identityPort.upsertUserRole(userId, department.getId(), vix.local.api.modules.identity.domain.model.UserRole.DEPT_ADMIN, true);
    }

    @Transactional
    public HrDepartment deactivateDepartment(UUID id) {
        HrDepartment department = getDepartmentById(id);
        department.deactivate();
        return hrDepartmentRepository.save(department);
    }
}
