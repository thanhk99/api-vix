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
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.model.UserStatus;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.shared.security.JwtUtil;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthApplicationService implements AuthPort {

    private final UserRepository userRepository;
    private final HrDepartmentRepository hrDepartmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

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

        return generateAuthResponseForUser(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw IdentityException.unauthorized("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String email;
        try {
            email = jwtUtil.extractEmail(refreshToken);
        } catch (Exception e) {
            throw IdentityException.unauthorized("Refresh token không hợp lệ");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> IdentityException.unauthorized("Tài khoản không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw IdentityException.unauthorized("Tài khoản đã bị khóa hoặc chưa kích hoạt");
        }

        return generateAuthResponseForUser(user);
    }

    private AuthResponse generateAuthResponseForUser(User user) {
        List<AuthResponse.DepartmentInfo> deptInfos = new ArrayList<>();
        String token = null;
        String route = null;
        
        List<String> roles = new ArrayList<>();
        if (user.getDepartmentRole() != null) {
            roles.add(user.getDepartmentRole().name());
        }

        if (user.getDepartmentRole() == UserRole.DIRECTOR) {
            // Giám đốc: không gắn phòng ban cụ thể, token không có deptId
            token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles, null, "shared");
            route = "bgd";
        } else if (user.getDepartmentId() != null) {
            HrDepartment hrDept = hrDepartmentRepository.findById(user.getDepartmentId()).orElse(null);
            if (hrDept != null) {
                AuthResponse.DepartmentInfo deptInfo = AuthResponse.DepartmentInfo.builder()
                        .deptId(hrDept.getId())
                        .deptName(hrDept.getName())
                        .deptCode(hrDept.getCode())
                        .schemaTarget("shared")
                        .build();
                deptInfos.add(deptInfo);
                
                token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles, deptInfo.getDeptId(), deptInfo.getSchemaTarget());
                route = deptInfo.getDeptCode().toLowerCase();
            }
        }

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(jwtUtil.generateRefreshToken(user.getEmail()))
                .user(userMapper.toUserInfo(user))
                .departments(deptInfos.isEmpty() ? null : deptInfos) // Trả về nếu có để FE tương thích
                .route(route)
                .build();
    }

    public AuthResponse selectDepartment(String email, UUID deptId) {
        // Method này giờ có thể không cần thiết nữa vì 1 user chỉ có 1 phòng ban,
        // Nhưng cứ implement để giữ api contract
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> IdentityException.unauthorized("Email hoặc mật khẩu không đúng"));

        boolean hasAccess = deptId.equals(user.getDepartmentId());
        
        List<String> roles = new ArrayList<>();
        if (user.getDepartmentRole() != null) {
            roles.add(user.getDepartmentRole().name());
        }

        if (!hasAccess) {
            throw IdentityException.unauthorized("Không có quyền truy cập phòng ban này");
        }

        HrDepartment hrDept = hrDepartmentRepository.findById(deptId)
                .orElseThrow(() -> IdentityException.unauthorized("Phòng ban không tồn tại"));

        String schemaTarget = "shared";
        String token = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles, deptId, schemaTarget);

        return AuthResponse.builder()
                .accessToken(token)
                .user(userMapper.toUserInfo(user))
                .route(hrDept.getCode().toLowerCase())
                .build();
    }

    public AuthResponse.UserInfo getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> IdentityException.unauthorized("User không tồn tại"));
        return userMapper.toUserInfo(user);
    }
    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(String email, String role) {
        return userRepository.findByEmail(email)
                .map(u -> u.getDepartmentRole() != null && u.getDepartmentRole().name().equals(role))
                .orElse(false);
    }
}
