package vix.local.api.modules.hr.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.api.v1.dto.request.CreateEmployeeRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateEmployeeRequest;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.domain.repository.HrPositionRepository;
import vix.local.api.modules.hr.domain.repository.HrUserRepository;
import vix.local.api.modules.identity.domain.model.UserDepartment;
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.repository.UserDepartmentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrEmployeeApplicationService {

    private final HrUserRepository hrUserRepository;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final HrPositionRepository hrPositionRepository;
    private final UserDepartmentRepository userDepartmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public HrUser createEmployee(CreateEmployeeRequest request) {
        if (hrUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw HrException.badRequest("Email đã tồn tại trong hệ thống");
        }

        HrDepartment department = hrDepartmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> HrException.notFound("Không tìm thấy phòng ban"));

        if (request.getPositionId() != null) {
            hrPositionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> HrException.notFound("Không tìm thấy chức danh"));
        }

        // Generate Employee Code (e.g. BGD001)
        long count = hrUserRepository.countByDepartmentId(department.getId());
        String newEmployeeCode = String.format("%s%03d", department.getCode().toUpperCase(), count + 1);

        if (hrUserRepository.findByEmployeeCode(newEmployeeCode).isPresent()) {
             newEmployeeCode = newEmployeeCode + "-" + System.currentTimeMillis() % 1000;
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        HrUser newUser = HrUser.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(encodedPassword)
                .status("ACTIVE")
                .employeeCode(newEmployeeCode)
                .phone(request.getPhone())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .idCardNumber(request.getIdCardNumber())
                .idCardIssuedDate(request.getIdCardIssuedDate())
                .idCardIssuedPlace(request.getIdCardIssuedPlace())
                .departmentId(request.getDepartmentId())
                .positionId(request.getPositionId())
                .joinDate(request.getJoinDate())
                .avatarUrl(request.getAvatarUrl())
                .build();

        HrUser savedUser = hrUserRepository.save(newUser);

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.MEMBER;
        if (role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN) {
            throw HrException.badRequest("Role không hợp lệ, chỉ cho phép MEMBER hoặc DEPT_ADMIN");
        }

        // Nếu role là trưởng phòng thì hạ trưởng phòng cũ xuống MEMBER (nếu có)
        if (role == UserRole.DEPT_ADMIN) {
            userDepartmentRepository.findManagerByDepartmentId(request.getDepartmentId()).ifPresent(oldManager -> {
                if (!oldManager.getUserId().equals(savedUser.getId())) {
                    userDepartmentRepository.upsert(oldManager.getUserId(), request.getDepartmentId(), UserRole.MEMBER, true);
                }
            });
        }

        // Tự động tạo UserDepartment với role để user có thể login
        UserDepartment userDepartment = UserDepartment.builder()
                .userId(savedUser.getId())
                .departmentId(request.getDepartmentId())
                .role(role)
                .isPrimary(true)
                .build();
        userDepartmentRepository.save(userDepartment);

        return savedUser;
    }

    @Transactional(readOnly = true)
    public Page<HrUser> searchEmployees(UUID departmentId, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return hrUserRepository.searchByKeyword(keyword, pageable);
        } else if (departmentId != null) {
            return hrUserRepository.findByDepartmentIdPaged(departmentId, pageable);
        } else {
            return hrUserRepository.findAllPaged(pageable);
        }
    }

    @Transactional(readOnly = true)
    public HrUser getEmployeeById(UUID id) {
        return hrUserRepository.findById(id)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));
    }

    @Transactional
    public HrUser updateEmployee(UUID id, UpdateEmployeeRequest request) {
        HrUser employee = getEmployeeById(id);

        if (request.getPositionId() != null && !request.getPositionId().equals(employee.getPositionId())) {
             hrPositionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> HrException.notFound("Không tìm thấy chức danh"));
             employee.setPositionId(request.getPositionId());
        }

        employee.setFullName(request.getFullName());
        employee.setPhone(request.getPhone());
        employee.setGender(request.getGender());
        employee.setBirthDate(request.getBirthDate());
        employee.setAddress(request.getAddress());
        employee.setIdCardNumber(request.getIdCardNumber());
        employee.setIdCardIssuedDate(request.getIdCardIssuedDate());
        employee.setIdCardIssuedPlace(request.getIdCardIssuedPlace());
        employee.setJoinDate(request.getJoinDate());
        employee.setAvatarUrl(request.getAvatarUrl());

        if (request.getRole() != null && employee.getDepartmentId() != null) {
            UserRole newRole = request.getRole();
            if (newRole == UserRole.ADMIN || newRole == UserRole.SUPER_ADMIN) {
                throw HrException.badRequest("Role không hợp lệ, chỉ cho phép MEMBER hoặc DEPT_ADMIN");
            }
            if (newRole == UserRole.DEPT_ADMIN) {
                userDepartmentRepository.findManagerByDepartmentId(employee.getDepartmentId()).ifPresent(oldManager -> {
                    if (!oldManager.getUserId().equals(employee.getId())) {
                        userDepartmentRepository.upsert(oldManager.getUserId(), employee.getDepartmentId(), UserRole.MEMBER, true);
                    }
                });
            }
            userDepartmentRepository.upsert(employee.getId(), employee.getDepartmentId(), newRole, true);
        }

        return hrUserRepository.save(employee);
    }

    @Transactional
    public HrUser transferDepartment(UUID employeeId, UUID newDeptId) {
        HrUser employee = getEmployeeById(employeeId);
        if (employee.getDepartmentId() != null && employee.getDepartmentId().equals(newDeptId)) {
            throw HrException.badRequest("Nhân viên đã ở phòng ban này");
        }

        HrDepartment department = hrDepartmentRepository.findById(newDeptId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy phòng ban mới"));

        long count = hrUserRepository.countByDepartmentId(newDeptId);
        String newEmployeeCode = String.format("%s%03d", department.getCode().toUpperCase(), count + 1);

        employee.transferTo(newDeptId, newEmployeeCode);
        HrUser saved = hrUserRepository.save(employee);

        // Cập nhật UserDepartment: xóa cũ và tạo mới theo phòng ban mới
        userDepartmentRepository.deleteByUserId(employeeId);
        userDepartmentRepository.upsert(employeeId, newDeptId, UserRole.MEMBER, true);

        return saved;
    }

    @Transactional
    public HrUser terminateEmployee(UUID employeeId) {
        HrUser employee = getEmployeeById(employeeId);
        employee.terminate();
        return hrUserRepository.save(employee);
    }

    @Transactional
    public HrUser deactivateEmployee(UUID employeeId) {
        HrUser employee = getEmployeeById(employeeId);
        employee.deactivate();
        return hrUserRepository.save(employee);
    }

    @Transactional
    public HrUser resetPassword(UUID employeeId, String newPassword) {
        HrUser employee = getEmployeeById(employeeId);
        employee.setPasswordHash(passwordEncoder.encode(newPassword));
        return hrUserRepository.save(employee);
    }
}
