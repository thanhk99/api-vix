package vix.local.api.modules.hr.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.api.v1.dto.request.CreateEmployeeRequest;
import vix.local.api.modules.hr.api.v1.dto.request.UpdateEmployeeRequest;
import vix.local.api.modules.hr.api.v1.dto.response.EmployeeDetailResponse;
import vix.local.api.modules.hr.api.v1.dto.response.EmployeeListItemResponse;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.model.HrPosition;
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.domain.repository.HrPositionRepository;
import vix.local.api.modules.hr.domain.repository.HrUserRepository;
import vix.local.api.modules.identity.application.port.IdentityPort;
import vix.local.api.modules.identity.domain.model.UserRole;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrEmployeeApplicationService {

    private final HrUserRepository hrUserRepository;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final HrPositionRepository hrPositionRepository;
    private final IdentityPort identityPort;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeDetailResponse createEmployee(CreateEmployeeRequest request) {
        if (hrUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw HrException.badRequest("Email đã tồn tại trong hệ thống");
        }

        HrDepartment department = hrDepartmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> HrException.notFound("Không tìm thấy phòng ban"));

        HrPosition position = null;
        if (request.getPositionId() != null) {
            position = hrPositionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> HrException.notFound("Không tìm thấy chức danh"));
        }

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

        if (role == UserRole.DEPT_ADMIN) {
            identityPort.demoteOldManager(request.getDepartmentId(), savedUser.getId());
        }

        identityPort.upsertUserRole(savedUser.getId(), request.getDepartmentId(), role, true);

        return toDetailResponse(savedUser, department, position, role);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeListItemResponse> searchEmployees(UUID departmentId, String keyword, Pageable pageable) {
        Page<HrUser> pagedResult;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pagedResult = hrUserRepository.searchByKeyword(keyword, pageable);
        } else if (departmentId != null) {
            pagedResult = hrUserRepository.findByDepartmentIdPaged(departmentId, pageable);
        } else {
            pagedResult = hrUserRepository.findAllPaged(pageable);
        }

        List<HrUser> users = pagedResult.getContent();
        if (users.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, pagedResult.getTotalElements());
        }

        List<UUID> deptIds = users.stream().map(HrUser::getDepartmentId).filter(Objects::nonNull).distinct()
                .collect(Collectors.toList());
        List<UUID> posIds = users.stream().map(HrUser::getPositionId).filter(Objects::nonNull).distinct()
                .collect(Collectors.toList());
        List<UUID> userIds = users.stream().map(HrUser::getId).collect(Collectors.toList());

        Map<UUID, HrDepartment> deptMap = hrDepartmentRepository.findAllById(deptIds).stream()
                .collect(Collectors.toMap(HrDepartment::getId, d -> d));
        Map<UUID, HrPosition> posMap = hrPositionRepository.findAllById(posIds).stream()
                .collect(Collectors.toMap(HrPosition::getId, p -> p));

        Map<UUID, UserRole> roleMap;
        if (departmentId != null) {
            roleMap = identityPort.getUserRoles(userIds, departmentId);
        } else {
            roleMap = users.stream()
                    .collect(Collectors.toMap(HrUser::getId,
                            u -> u.getDepartmentId() != null ? identityPort.getUserRole(u.getId(), u.getDepartmentId())
                                    : UserRole.MEMBER));
        }

        List<EmployeeListItemResponse> content = users.stream().map(u -> {
            HrDepartment d = u.getDepartmentId() != null ? deptMap.get(u.getDepartmentId()) : null;
            HrPosition p = u.getPositionId() != null ? posMap.get(u.getPositionId()) : null;
            UserRole r = roleMap.getOrDefault(u.getId(), UserRole.MEMBER);
            return toListItemResponse(u, d, p, r);
        }).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, pagedResult.getTotalElements());
    }

    @Transactional(readOnly = true)
    public EmployeeDetailResponse getEmployeeDetailById(UUID id) {
        HrUser user = hrUserRepository.findById(id)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));

        HrDepartment dept = user.getDepartmentId() != null
                ? hrDepartmentRepository.findById(user.getDepartmentId()).orElse(null)
                : null;
        HrPosition pos = user.getPositionId() != null ? hrPositionRepository.findById(user.getPositionId()).orElse(null)
                : null;
        UserRole role = user.getDepartmentId() != null ? identityPort.getUserRole(user.getId(), user.getDepartmentId())
                : null;

        return toDetailResponse(user, dept, pos, role);
    }

    @Transactional
    public EmployeeDetailResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        HrUser employee = hrUserRepository.findById(id)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));

        HrPosition position = null;
        if (request.getPositionId() != null && !request.getPositionId().equals(employee.getPositionId())) {
            position = hrPositionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> HrException.notFound("Không tìm thấy chức danh"));
            employee.setPositionId(request.getPositionId());
        } else if (employee.getPositionId() != null) {
            position = hrPositionRepository.findById(employee.getPositionId()).orElse(null);
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

        UserRole role = null;
        if (request.getRole() != null && employee.getDepartmentId() != null) {
            role = request.getRole();
            if (role == UserRole.DEPT_ADMIN) {
                identityPort.demoteOldManager(employee.getDepartmentId(), employee.getId());
            }
            identityPort.upsertUserRole(employee.getId(), employee.getDepartmentId(), role, true);
        } else if (employee.getDepartmentId() != null) {
            role = identityPort.getUserRole(employee.getId(), employee.getDepartmentId());
        }

        HrUser saved = hrUserRepository.save(employee);
        HrDepartment dept = saved.getDepartmentId() != null
                ? hrDepartmentRepository.findById(saved.getDepartmentId()).orElse(null)
                : null;
        return toDetailResponse(saved, dept, position, role);
    }

    @Transactional
    public EmployeeDetailResponse transferDepartment(UUID employeeId, UUID newDeptId) {
        HrUser employee = hrUserRepository.findById(employeeId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));

        if (employee.getDepartmentId() != null && employee.getDepartmentId().equals(newDeptId)) {
            throw HrException.badRequest("Nhân viên đã ở phòng ban này");
        }

        HrDepartment department = hrDepartmentRepository.findById(newDeptId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy phòng ban mới"));

        long count = hrUserRepository.countByDepartmentId(newDeptId);
        String newEmployeeCode = String.format("%s%03d", department.getCode().toUpperCase(), count + 1);

        employee.transferTo(newDeptId, newEmployeeCode);
        HrUser saved = hrUserRepository.save(employee);

        identityPort.deleteUserDepartment(employeeId);
        identityPort.upsertUserRole(employeeId, newDeptId, UserRole.MEMBER, true);

        HrPosition position = saved.getPositionId() != null
                ? hrPositionRepository.findById(saved.getPositionId()).orElse(null)
                : null;
        return toDetailResponse(saved, department, position, UserRole.MEMBER);
    }

    @Transactional
    public EmployeeDetailResponse terminateEmployee(UUID employeeId) {
        HrUser employee = hrUserRepository.findById(employeeId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));
        employee.terminate();
        HrUser saved = hrUserRepository.save(employee);
        return getEmployeeDetailById(saved.getId());
    }

    @Transactional
    public EmployeeDetailResponse deactivateEmployee(UUID employeeId) {
        HrUser employee = hrUserRepository.findById(employeeId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));
        employee.deactivate();
        HrUser saved = hrUserRepository.save(employee);
        return getEmployeeDetailById(saved.getId());
    }

    @Transactional
    public void resetPassword(UUID employeeId, String newPassword) {
        HrUser employee = hrUserRepository.findById(employeeId)
                .orElseThrow(() -> HrException.notFound("Không tìm thấy nhân viên"));
        employee.setPasswordHash(passwordEncoder.encode(newPassword));
        hrUserRepository.save(employee);
    }

    private EmployeeDetailResponse toDetailResponse(HrUser user, HrDepartment dept, HrPosition pos, UserRole role) {
        return EmployeeDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .employeeCode(user.getEmployeeCode())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .idCardNumber(user.getIdCardNumber())
                .idCardIssuedDate(user.getIdCardIssuedDate())
                .idCardIssuedPlace(user.getIdCardIssuedPlace())
                .departmentId(user.getDepartmentId())
                .departmentName(dept != null ? dept.getName() : null)
                .departmentCode(dept != null ? dept.getCode() : null)
                .positionId(user.getPositionId())
                .positionName(pos != null ? pos.getName() : null)
                .positionCode(pos != null ? pos.getCode() : null)
                .role(role)
                .joinDate(user.getJoinDate())
                .terminateDate(user.getTerminateDate())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private EmployeeListItemResponse toListItemResponse(HrUser user, HrDepartment dept, HrPosition pos, UserRole role) {
        return EmployeeListItemResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .employeeCode(user.getEmployeeCode())
                .departmentId(user.getDepartmentId())
                .departmentName(dept != null ? dept.getName() : null)
                .departmentCode(dept != null ? dept.getCode() : null)
                .positionId(user.getPositionId())
                .positionName(pos != null ? pos.getName() : null)
                .positionCode(pos != null ? pos.getCode() : null)
                .role(role)
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
