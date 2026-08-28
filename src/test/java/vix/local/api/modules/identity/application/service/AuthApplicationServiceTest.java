package vix.local.api.modules.identity.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.identity.api.v1.dto.response.AuthResponse;
import vix.local.api.modules.identity.application.mapper.UserMapper;
import vix.local.api.modules.identity.domain.exception.IdentityException;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.model.UserStatus;
import vix.local.api.modules.identity.domain.repository.UserRepository;
import vix.local.api.shared.security.JwtUtil;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HrDepartmentRepository hrDepartmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthApplicationService authService;

    private User activeUser;
    private final String validRefreshToken = "valid-refresh-token";
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setId(UUID.randomUUID());
        activeUser.setEmail(email);
        activeUser.setStatus(UserStatus.ACTIVE);
        activeUser.setDepartmentRole(vix.local.api.modules.identity.domain.model.UserRole.DIRECTOR);
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewTokens() {
        // Arrange
        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(validRefreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(activeUser));
        when(jwtUtil.generateAccessToken(eq(activeUser.getId()), eq(email), any(), isNull(), eq("shared"))).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(email)).thenReturn("new-refresh-token");
        when(userMapper.toUserInfo(activeUser)).thenReturn(new AuthResponse.UserInfo());

        // Act
        AuthResponse response = authService.refreshToken(validRefreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(jwtUtil).generateAccessToken(eq(activeUser.getId()), eq(email), any(), isNull(), eq("shared"));
        verify(jwtUtil).generateRefreshToken(email);
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        // Arrange
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        // Act & Assert
        IdentityException exception = assertThrows(IdentityException.class, 
            () -> authService.refreshToken("invalid-token"));
        assertEquals("Refresh token không hợp lệ hoặc đã hết hạn", exception.getMessage());
    }

    @Test
    void refreshToken_UserNotFound_ThrowsException() {
        // Arrange
        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(validRefreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        IdentityException exception = assertThrows(IdentityException.class, 
            () -> authService.refreshToken(validRefreshToken));
        assertEquals("Tài khoản không tồn tại", exception.getMessage());
    }

    @Test
    void refreshToken_UserInactive_ThrowsException() {
        // Arrange
        activeUser.setStatus(UserStatus.INACTIVE);
        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtUtil.extractEmail(validRefreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(activeUser));

        // Act & Assert
        IdentityException exception = assertThrows(IdentityException.class, 
            () -> authService.refreshToken(validRefreshToken));
        assertEquals("Tài khoản đã bị khóa hoặc chưa kích hoạt", exception.getMessage());
    }
}
