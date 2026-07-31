package vix.local.api.modules.identity.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.identity.api.v1.dto.request.LoginRequest;
import vix.local.api.modules.identity.api.v1.dto.response.AuthResponse;
import vix.local.api.modules.identity.application.mapper.UserMapper;
import vix.local.api.modules.identity.application.port.AuthPort;
import vix.local.api.modules.identity.domain.exception.IdentityException;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.model.UserDepartment;
import vix.local.api.modules.identity.domain.model.UserStatus;
import vix.local.api.modules.identity.domain.repository.UserDepartmentRepository;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.permission.application.service.ModulePermissionService;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.shared.security.JwtUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthApplicationService implements AuthPort {

    private final UserRepository userRepository;
    private final UserDepartmentRepository userDepartmentRepository;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ModulePermissionService modulePermissionService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> IdentityException.unauthorized("Email hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw IdentityException.unauthorized("Email hoặc mật khẩu không đúng");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw IdentityException.unauthorized("Tài khoản đã bị khóa hoặc chưa kích hoạt");
        }

        List<UserDepartment> userDepts = userDepartmentRepository.findByUserId(user.getId());

        List<AuthResponse.DepartmentInfo> deptInfos = userDepts.stream().map(ud -> {
            HrDepartment hrDept = hrDepartmentRepository.findById(ud.getDepartmentId()).orElse(null);
            if (hrDept == null)
                return null;
            return AuthResponse.DepartmentInfo.builder()
                    .deptId(hrDept.getId())
                    .deptName(hrDept.getName())
                    .deptCode(hrDept.getCode())
                    .schemaTarget("shared")
                    .build();
        }).filter(d -> d != null).collect(Collectors.toList());

        String token = null;
        String route = null;

        List<String> roles = userDepts.stream().map(d -> d.getRole().name()).collect(Collectors.toList());

        if (deptInfos.size() == 1) {
            AuthResponse.DepartmentInfo dept = deptInfos.get(0);
            token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles, dept.getDeptId(),
                    dept.getSchemaTarget());
            route = dept.getDeptCode().toLowerCase();
        } else if (deptInfos.isEmpty() && hasGlobalSuperAdminRole(roles)) {
            token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), List.of("SUPER_ADMIN"), null, "shared");
        }

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(jwtUtil.generateRefreshToken(user.getEmail()))
                .user(userMapper.toUserInfo(user, deptInfos.size() == 1 ? deptInfos.get(0).getDeptId() : null,
                        userDepts))
                .departments(deptInfos.size() > 1 ? deptInfos : null)
                .route(route)
                .build();
    }

    public AuthResponse selectDepartment(String email, UUID deptId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> IdentityException.unauthorized("Email hoặc mật khẩu không đúng"));

        List<UserDepartment> userDepts = userDepartmentRepository.findByUserId(user.getId());
        boolean hasAccess = userDepts.stream().anyMatch(ud -> ud.getDepartmentId().equals(deptId));
        List<String> roles = userDepts.stream().map(d -> d.getRole().name()).collect(Collectors.toList());

        if (!hasAccess && !hasGlobalSuperAdminRole(roles)) {
            throw IdentityException.unauthorized("Không có quyền truy cập phòng ban này");
        }

        HrDepartment hrDept = hrDepartmentRepository.findById(deptId)
                .orElseThrow(() -> IdentityException.unauthorized("Phòng ban không tồn tại"));

        String schemaTarget = "shared";
        String token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles, deptId, schemaTarget);

        return AuthResponse.builder()
                .accessToken(token)
                .user(userMapper.toUserInfo(user, deptId, userDepts))
                .route(hrDept.getCode().toLowerCase())
                .build();
    }

    private boolean hasGlobalSuperAdminRole(List<String> roles) {
        return roles.contains("SUPER_ADMIN");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(String email, String role) {
        return userRepository.findByEmail(email)
                .map(u -> userDepartmentRepository.findByUserId(u.getId()).stream()
                        .anyMatch(d -> d.getRole().name().equals(role)))
                .orElse(false);
    }
}
